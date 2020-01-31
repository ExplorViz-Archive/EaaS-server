package net.explorviz.eaas.api;

import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.service.docker.AdapterException;
import net.explorviz.eaas.service.docker.DockerAdapter;
import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.repository.BuildRepository;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.security.APIAuthenticator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import static net.explorviz.eaas.security.APIAuthenticator.SECRET_HEADER;

@RestController
@Slf4j
public class ProjectsController {
    private final ProjectRepository projectRepository;
    private final BuildRepository buildRepository;
    private final APIAuthenticator apiAuthenticator;
    private final DockerAdapter dockerAdapter;

    public ProjectsController(ProjectRepository projectRepository, BuildRepository buildRepository,
                              APIAuthenticator apiAuthenticator, DockerAdapter dockerAdapter) {
        this.projectRepository = projectRepository;
        this.buildRepository = buildRepository;
        this.apiAuthenticator = apiAuthenticator;
        this.dockerAdapter = dockerAdapter;
    }

    /**
     * Lists all projects hosted on this instance. Only contains non-hidden projects, even if the request contains a
     * valid secret for a hidden project.
     */
    @RequestMapping(path = "/api/v1/projects", method = RequestMethod.GET)
    public Map<String, Object> getProjects() {
        return Collections.singletonMap("projects", projectRepository.findByHidden(false));
    }

    @RequestMapping(path = "/api/v1/projects/{project}", method = RequestMethod.GET)
    public Map<String, Object> getProject(@RequestHeader(value = SECRET_HEADER, required = false) String secret,
                                          @PathVariable("project") long projectId) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, true);

        return Collections.singletonMap("project", project);
    }

    @RequestMapping(path = "/api/v1/projects/{project}/builds", method = RequestMethod.GET)
    public Map<String, Object> getProjectBuilds(@RequestHeader(value = SECRET_HEADER, required = false) String secret,
                                                @PathVariable("project") long projectId) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, true);

        return Collections.singletonMap("builds", project.getBuilds());
    }

    @RequestMapping(path = "/api/v1/projects/{project}/builds/{build}", method = RequestMethod.GET)
    public Map<String, Object> getProjectBuild(@RequestHeader(value = SECRET_HEADER, required = false) String secret,
                                               @PathVariable("project") long projectId,
                                               @PathVariable("build") long buildId) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, true);

        Build build = buildRepository.findByProjectAndId(project, buildId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Build not found"));

        return Collections.singletonMap("build", build);
    }

    /**
     * To simplify the build upload script this method doesn't use JSON but form data for input and outputs the
     * resulting path as plain text.
     */
    @RequestMapping(path = "/api/v1/projects/{project}/builds", method = RequestMethod.POST, produces = "text/plain")
    public String postProjectBuild(@RequestHeader(SECRET_HEADER) String secret,
                                   @PathVariable("project") long projectId,
                                   @RequestParam("name") String name,
                                   @RequestParam("imageID") String imageID,
                                   @RequestParam("image") MultipartFile image) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, false);

        if (name.length() < Build.NAME_MIN_LENGTH || name.length() > Build.NAME_MAX_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Build name must be between " +
                Build.NAME_MIN_LENGTH + " and " + Build.NAME_MAX_LENGTH + " characters long!");
        }

        log.info("Receiving new build for project #{} ('{}') with name '{}'", projectId, project.getName(), name);
        log.debug("Image has size {} B, original filename '{}'", image.getSize(), image.getOriginalFilename());

        try (InputStream stream = image.getInputStream()) {
            dockerAdapter.loadImage(stream);
        } catch (AdapterException e) {
            log.error("Loading image of new build in docker failed", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Docker couldn't load image");
        } catch (IOException e) {
            log.error("Reading image of new build from client failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "I/O Error while receiving the image");
        }

        /*
         * TODO: docker-java doesn't tell us the ID of the image we just loaded, so right now we have to trust the
         *       client. This shouldn't be a problem since they could upload anything they wanted anyway. Leaking
         *       'secret' images present in the host system also seems unlikely.
         */
        Build build = buildRepository.save(new Build(name, project, imageID));
        return "/projects/" + project.getId() + "/builds/" + build.getId();
    }

    /**
     * Fetches the {@link Project} with the given id from the database, or raise a Not Found error to the client if it
     * doesn't exist.
     */
    private Project findProjectById(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }
}
