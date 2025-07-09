package at.kalwoda.nocodeapi.javalin;

import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.domain.Project;
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
@RequestMapping("/api/generator")
public class GeneratorController {

    private final JavalinLauncherService javalinLauncherService;

    public GeneratorController(JavalinLauncherService javalinLauncherService) {
        this.javalinLauncherService = javalinLauncherService;
    }

    @PostMapping("/start/{projectApiKey}")
    public ResponseEntity<List<Map<String, Object>>> startLiveApi(Authentication authentication, @PathVariable String projectApiKey) {

        List<Map<String, Object>> infos = javalinLauncherService.setupApi(authentication.getName(), projectApiKey);

        return ResponseEntity.ok(infos);
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopLiveApi(
            @RequestParam String userId,
            @RequestParam String entityName
    ) {
        boolean stopped = javalinLauncherService.stopApi(userId, entityName);
        if (stopped) {
            return ResponseEntity.ok("API stopped for " + entityName);
        }
        return ResponseEntity.status(404).body("API not found for " + entityName);
    }
}
