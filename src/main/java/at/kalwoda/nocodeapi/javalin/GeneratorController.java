package at.kalwoda.nocodeapi.javalin;

import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.presentation.ApiConstants;
import at.kalwoda.nocodeapi.service.ProjectService;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API + "/generator")
public class GeneratorController {

    private final JavalinLauncherService javalinLauncherService;
    private final ProjectService projectService;

    public GeneratorController(JavalinLauncherService javalinLauncherService, ProjectService projectService) {
        this.javalinLauncherService = javalinLauncherService;
        this.projectService = projectService;
    }

    @PostMapping("/start/{projectApiKey}")
    public ResponseEntity<List<Map<String, Object>>> startLiveApi(Authentication authentication, @PathVariable String projectApiKey) {

        List<Map<String, Object>> infos = javalinLauncherService.setupApi(authentication.getName(), projectApiKey);

        return ResponseEntity.ok(infos);
    }

    @PostMapping("/stop/{projectApiKey}")
    public ResponseEntity<?> stopLiveApi(
            Authentication authentication,
            @PathVariable String projectApiKey
    ) {
        boolean stopped = javalinLauncherService.stopApi(authentication.getName(), projectApiKey);
        if (stopped) {
            return ResponseEntity.ok("API stopped for " + projectApiKey);
        }
        return ResponseEntity.status(404).body("API not found for " + projectApiKey);
    }

    @GetMapping("/status/{projectApiKey}")
    public ResponseEntity<?> getApiStatus(
            Authentication authentication,
            @PathVariable String projectApiKey
    ) {
        Project project = projectService.checkProjectOwnership(authentication.getName(), projectApiKey);

        Map<String, Object> status = new HashMap<>();
        status.put("projectApiKey", project.getApiKey().value());
        status.put("isRunning", javalinLauncherService.isApiRunning(project.getApiKey().value()));

        return ResponseEntity.ok(status);
    }
}
