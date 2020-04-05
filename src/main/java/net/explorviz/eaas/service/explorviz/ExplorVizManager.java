package net.explorviz.eaas.service.explorviz;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.docker.compose.DockerComposeAdapter;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Lazy
@Slf4j
public final class ExplorVizManager {
    /**
     * For each version, a corresponding docker-compose file in resources/ has to be available
     */
    public static final List<String> EXPLORVIZ_VERSIONS = List.of("1.5.0", "dev");

    private static final int PORT_MAX = 65535;

    private final DockerComposeAdapter dockerCompose;
    private final int maxInstances;
    private final int frontendPortOffset;
    private final String accessUrlTemplate;

    @Getter
    private final String defaultVersion;

    /*
     * Use concurrent collections instead of synchronizing #stopInstance() so we can have any amount of stops as well as
     * one start running concurrently. The only scenario not allowed is two instances starting at the same time, because
     * our port selection logic in #startInstance() cannot handle that.
     */
    private final ConcurrentMap<Integer, ExplorVizInstance> instances;
    private final ConcurrentMap<Long, ExplorVizInstance> instancesByBuildId;

    /**
     * Keep track of which ID to use next in order to use all ports equally. Will decay over time so not as good as LRU
     * but better than always starting from index 0.
     */
    private volatile int nextInstance;

    public ExplorVizManager(DockerComposeAdapter dockerCompose,
                            @Value("${eaas.explorviz.maxInstances}") int maxInstances,
                            @Value("${eaas.explorviz.frontendPortOffset}") int frontendPortOffset,
                            @Value("${eaas.explorviz.accessUrlTemplate}")
                                String accessUrlTemplate,
                            @Value("${eaas.explorviz.defaultVersion}") String defaultVersion) {
        Validate.inclusiveBetween(1, Integer.MAX_VALUE, maxInstances, "Option eaas.explorviz.maxInstances must be at " +
            "least 1");
        Validate.inclusiveBetween(1, PORT_MAX, frontendPortOffset, "Option eaas.explorviz.frontendPortOffset is not a" +
            " valid port");
        Validate.inclusiveBetween(1, PORT_MAX, frontendPortOffset + maxInstances, "Option eaas.explorviz" +
            ".frontendPortOffset plus maxInstances exceed valid ports");
        Validate.notBlank(accessUrlTemplate, "Option eaas.explorviz.accessUrlTemplate may not be empty");
        Validate.isTrue(EXPLORVIZ_VERSIONS.contains(defaultVersion), "Option eaas.explorviz.defaultVersion specifies " +
            "unknown version");

        log.info("Given port range {}-{} for ExplorViz instances (max {} instances)", frontendPortOffset,
            frontendPortOffset + maxInstances - 1, maxInstances);

        this.dockerCompose = dockerCompose;
        this.maxInstances = maxInstances;
        this.frontendPortOffset = frontendPortOffset;
        this.accessUrlTemplate = accessUrlTemplate;
        this.defaultVersion = defaultVersion;

        this.instances = new ConcurrentHashMap<>(maxInstances);
        this.instancesByBuildId = new ConcurrentHashMap<>(maxInstances);
    }

    // TODO: Support multiple instances of the same build? - requires changes in frontend as well

    /**
     * @throws AdapterException Exceptions of this kind are also logged before they are re-thrown.
     */
    public synchronized ExplorVizInstance startInstance(@NonNull Build build, @NonNull String version)
        throws AdapterException, NoMoreSlotsException {
        Validate.notBlank(version, "version may not be empty");

        if (instances.size() >= maxInstances) {
            throw new NoMoreSlotsException("Won't start another ExplorViz instance, " + instances.size() + "/"
                + maxInstances + " instances are running");
        }

        log.debug("Requested ExplorViz instance for build #{} '{}'", build.getId(), build.getName());

        int id = nextInstance;
        /*
         * This is guaranteed to terminate because we know we have less than maxInstance ports used and instances can
         * only be stopped while this method is running.
         */
        while (instances.get(id) != null) {
            id = (id + 1) % maxInstances;
        }
        nextInstance = (id + 1) % maxInstances;

        int frontendPort = frontendPortOffset + id;
        String accessUrl = accessUrlTemplate.replace("%FRONTEND_PORT%", Integer.toString(frontendPort));

        ExplorVizInstance instance = new ExplorVizInstance(buildInstanceName(id, build), id, build, version,
            frontendPort, accessUrl);

        log.info("Starting instance {} (#{}) on port {}", instance.getName(), instance.getId(),
            instance.getFrontendPort());

        try {
            dockerCompose.up(instance);
        } catch (AdapterException e) {
            log.error("Error starting ExplorViz instance", e);
            throw e;
        }

        instances.put(id, instance);
        instancesByBuildId.put(build.getId(), instance);
        return instance;
    }

    public Optional<ExplorVizInstance> getInstance(@NonNull Build build) {
        return Optional.ofNullable(instancesByBuildId.get(build.getId()));
    }

    /**
     * @throws AdapterException Exceptions of this kind are also logged before they are re-thrown.
     */
    public void stopInstance(@NonNull ExplorVizInstance instance) throws AdapterException {
        synchronized (instance) {
            if (!instance.isRunning()) {
                log.warn("Tried to stop already stopped instance {}", instance.getName());
                throw new AdapterException("Instance " + instance.getName() + " is already stopped");
            }

            log.info("Stopping instance {} (#{}) on port {}", instance.getName(), instance.getId(),
                instance.getFrontendPort());

            try {
                dockerCompose.down(instance);
            } catch (AdapterException e) {
                log.error("Error stopping ExplorViz instance", e);
                throw e;
            }

            instances.remove(instance.getId());
            instancesByBuildId.remove(instance.getBuild().getId());
        }
    }

    /**
     * Build a unique name for this instance, so we can keep track of and cleanly separate multiple docker-compose
     * instances.
     *
     * @param instanceID Internal id for the instance, only unique while it is running
     * @param build      The build this instance is for
     */
    private static String buildInstanceName(int instanceID, @NonNull Build build) {
        return "eaas-" + instanceID + "-" + build.getId();
    }

    /**
     * Stops all currently running instances. Does not prevent new instances from starting at the same time. Such
     * instances will neither be stopped nor lead to an error. {@link AdapterException} occuring during shutdown are
     * logged but not rethrown.
     * <p>
     * This is also automatically called by the dependency injection framework on application shutdown.
     *
     * @return {@code true} if all instances that were running when this method was called stopped correctly.
     */
    @PreDestroy
    public boolean stopAllInstances() {
        log.info("Requested stop of all running instances");

        // allMatch() is specified to allow short-circuit evaluation but this shouldn't be possible here
        return instances.values().parallelStream().allMatch(instance -> {
            try {
                stopInstance(instance);
                return true;
            } catch (AdapterException e) {
                return false;
            }
        });
    }

    /**
     * Updates to the instances after this method was called are not reflected in the returned list.
     *
     * @return A read-only representation of all instances currently running
     */
    public Collection<ExplorVizInstance> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }
}
