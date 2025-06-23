package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.service.ProjectService;
import at.kalwoda.nocodeapi.service.commands.ProjectCommands;
import at.kalwoda.nocodeapi.service.commands.ProjectCommands.CreateProjectCommand;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectDto;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectMinimalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API + "/projects")
public class ProjectRestController {
    private final ProjectService projectService;

    @GetMapping("/getProjects")
    public List<ProjectDto> getProjects(Authentication authentication) {
        log.info("Fetching all projects");
        return projectService.getAllProjects(authentication.getName())
                .stream()
                .map(ProjectDto::new)
                .toList();
    }

    @GetMapping("/getProjects/minimal")
    public List<ProjectMinimalDto> getProjectsMinimal(Authentication authentication) {
        log.info("Fetching all projects");
        return projectService.getAllProjects(authentication.getName())
                .stream()
                .map(ProjectMinimalDto::new)
                .toList();
    }

    @GetMapping("/getProject/{apiKey}")
    public ResponseEntity<ProjectDto> getProject(Authentication authentication, @PathVariable String apiKey) {
        return ResponseEntity.ok(new ProjectDto(projectService.getProject(authentication.getName(), apiKey)));
    }

    @GetMapping("/getProject/{apiKey}/minimal")
    public ResponseEntity<ProjectMinimalDto> getProjectMinimal(Authentication authentication, @PathVariable String apiKey) {
        return ResponseEntity.ok(new ProjectMinimalDto(projectService.getProject(authentication.getName(), apiKey)));
    }

    @PostMapping("/createProject")
    public ResponseEntity<ProjectMinimalDto> createProject(Authentication authentication, @RequestBody CreateProjectCommand command) {
        var project = projectService.createProject(authentication.getName(), command);
        return ResponseEntity.ok(new ProjectMinimalDto(project));
    }
}
