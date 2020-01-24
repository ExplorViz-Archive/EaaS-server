package net.explorviz.eaas.explorviz;

import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.docker.AdapterException;
import net.explorviz.eaas.docker.DockerComposeAdapter;
import net.explorviz.eaas.model.Build;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Lazy
@Slf4j
public final class ExplorVizManager {
    private static final int PORT_MAX = 65535;

    private final DockerComposeAdapter dockerCompose;
    private final int maxInstances;
    private final int frontendPortOffset;
    private final String accessUrlTemplate;

    /*
     * Use concurrent collections instead of synchronized on #stopInstance() so we can have any amount of stops as
     * well as one start running concurrently. The only scenario not allowed is two instances starting at the same time,
     * because our port selection logic in #startInstance() cannot handle that.
     */
    private final ConcurrentMap<Integer, ExplorVizInstance> instances = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, ExplorVizInstance> instancesByBuildId = new ConcurrentHashMap<>();
    /**
     * Keep track of which ID to use next in order to use all ports equally. Will decay over time so not as good as LRU
     * but better than always starting from index 0.
     */
    private volatile int nextInstance = 0;

    public ExplorVizManager(DockerComposeAdapter dockerCompose,
                            @Value("${eaas.explorviz.maxInstances:10}") int maxInstances,
                            @Value("${eaas.explorviz.frontendPortOffset:8800}") int frontendPortOffset,
                            @Value("${eaas.explorviz.accessUrlTemplate:http://localhost:%FRONTEND_PORT%}") String accessUrlTemplate) {
        Validate.inclusiveBetween(1, Integer.MAX_VALUE, maxInstances);
        Validate.inclusiveBetween(1, PORT_MAX, maxInstances);
        Validate.inclusiveBetween(1, PORT_MAX, maxInstances);
        Validate.notBlank(accessUrlTemplate, "accessUrlTemplate may not be empty");

        log.info("Given port range {}-{} for ExplorViz instances (max {} instances)", frontendPortOffset,
            frontendPortOffset + maxInstances, maxInstances);

        this.dockerCompose = dockerCompose;
        this.maxInstances = maxInstances;
        this.frontendPortOffset = frontendPortOffset;
        this.accessUrlTemplate = accessUrlTemplate;
    }

    public synchronized ExplorVizInstance startInstance(@NonNull Build build, @NonNull String version)
        throws AdapterException, NoMoreSlotsException {
        if (instances.size() >= maxInstances) {
            throw new NoMoreSlotsException("Won't start another ExplorViz instance, " + instances.size() + "/"
                + maxInstances + " instances are running");
        }

        log.info("Requested ExplorViz instance for build #{} '{}' of project #{} '{}'", build.getId(), build.getName(),
            build.getProject().getId(), build.getProject().getName());

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

        ExplorVizInstance instance = new ExplorVizInstance(id, build.getId(), version, buildInstanceName(id, build),
            frontendPort, accessUrl, build.getImageID());

        log.info("Starting instance {} (#{}) on port {}", instance.getName(), instance.getId(),
            instance.getFrontendPort());

        dockerCompose.up(instance.getName(), instance.getComposeDefinition());

        instances.put(id, instance);
        instancesByBuildId.put(instance.getBuildId(), instance);
        return instance;
    }

    public Optional<ExplorVizInstance> getInstance(@NonNull Build build) {
        return Optional.ofNullable(instancesByBuildId.get(build.getId()));
    }

    public void stopInstance(@NonNull ExplorVizInstance instance) throws AdapterException {
        log.info("Stopping instance {} (#{}) on port {}", instance.getName(), instance.getId(),
            instance.getFrontendPort());

        dockerCompose.down(instance.getName(), instance.getComposeDefinition());
        instances.remove(instance.getId());
        instancesByBuildId.remove(instance.getBuildId());
    }

    /**
     * Build a unique name for this instance, so we can keep track of and clenaly separate multiple docker-compose
     * instances.
     *
     * @param instanceID Internal id for the instance, only unique while it is running
     * @param build      The build this instance is for
     */
    private static String buildInstanceName(int instanceID, @NonNull Build build) {
        return "eaas-" + instanceID + "-p" + build.getProject().getId() + "-b" + build.getId();
    }

    public void stopAllInstances() throws AdapterException {
        log.info("Requested stop of all running instances");

        for (ExplorVizInstance instance : instances.values()) {
            stopInstance(instance);
        }
    }

    /**
     * @return A read-only representation of all instances currently running. Updates to the instances after this method
     * was called are not reflected in the returned list.
     */
    public Collection<ExplorVizInstance> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }
}
