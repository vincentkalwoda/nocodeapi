package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.service.ProjectService;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API + "/projects")
public class ProjectRestController {
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectDto> getProjects(Authentication authentication) {
        log.info("Fetching all projects");
        return projectService.getAllProjects(authentication.getName())
                .stream()
                .map(ProjectDto::new)
                .toList();
    }

    @GetMapping("/{apiKey}")
    public ResponseEntity<ProjectDto> getProject(Authentication authentication, @RequestParam String apiKey) {
        return ResponseEntity.ok(new ProjectDto(projectService.getProject(authentication.getName(), apiKey)));
    }
}
