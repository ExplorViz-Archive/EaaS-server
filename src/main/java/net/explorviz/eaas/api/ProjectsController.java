package net.explorviz.eaas.api;

import net.explorviz.eaas.model.Build;
import net.explorviz.eaas.model.Project;
import net.explorviz.eaas.repository.BuildRepository;
import net.explorviz.eaas.repository.ProjectRepository;
import net.explorviz.eaas.security.APIAuthenticator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

import static net.explorviz.eaas.security.APIAuthenticator.SECRET_HEADER_NAME;

@RestController
public class ProjectsController {


    private final ProjectRepository projectRepository;
    private final BuildRepository buildRepository;
    private final APIAuthenticator apiAuthenticator;

    public ProjectsController(ProjectRepository projectRepository, BuildRepository buildRepository,
                              APIAuthenticator apiAuthenticator) {
        this.projectRepository = projectRepository;
        this.buildRepository = buildRepository;
        this.apiAuthenticator = apiAuthenticator;
    }

    /**
     * Lists all projects hosted on this instance. Only contains non-hidden projects, even if the request contains a
     * valid secret for a hidden project.
     */
    @RequestMapping(path = "/api/v1/projects", method = RequestMethod.GET)
    public Map<String, Object> getProjects() {
        return Collections.singletonMap("projects", projectRepository.findAll());
    }

    @RequestMapping(path = "/api/v1/projects/{project}", method = RequestMethod.GET)
    public Map<String, Object> getProject(@RequestHeader(value = SECRET_HEADER_NAME, required = false) String secret,
                                          @PathVariable("project") long projectId) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, true);

        return Collections.singletonMap("project", project);
    }

    @RequestMapping(path = "/api/v1/projects/{project}/builds", method = RequestMethod.GET)
    public Map<String, Object> getProjectBuilds(@RequestHeader(value = SECRET_HEADER_NAME, required = false) String secret,
                                                @PathVariable("project") long projectId) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, true);

        return Collections.singletonMap("builds", project.getBuilds());
    }

    @RequestMapping(path = "/api/v1/projects/{project}/builds/{build}", method = RequestMethod.GET)
    public Map<String, Object> getProjectBuild(@RequestHeader(value = SECRET_HEADER_NAME, required = false) String secret,
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
    public String postProjectBuild(@RequestHeader(SECRET_HEADER_NAME) String secret,
                                   @PathVariable("project") long projectId,
                                   @RequestParam("name") String name) {
        Project project = findProjectById(projectId);
        apiAuthenticator.authorizeRequest(project, secret, false);

        if (name.length() < Build.NAME_MIN_LENGTH || name.length() > Build.NAME_MAX_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Build name must be between " +
                Build.NAME_MIN_LENGTH + " and " + Build.NAME_MAX_LENGTH + " characters long!");
        }

        // TODO: Upload image

        Build build = buildRepository.save(new Build(name, project, "abcdef"));
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
