package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.AIService;
import at.kalwoda.nocodeapi.service.commands.ModelCommands;
import at.kalwoda.nocodeapi.service.dtos.prompthistory.PromptHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API + "/ai")
@RequiredArgsConstructor
public class AIRestController {
    private final AIService aiService;

    @PostMapping("/generateModel/{projectApiKey}")
    public ResponseEntity<List<PromptHistoryDto>> generate(Authentication authentication, @PathVariable String projectApiKey, @RequestBody String description) {
        return ResponseEntity.ok(aiService.generateModelFromPrompt(authentication.getName(), projectApiKey, description));
    }

    @PostMapping("/generateEndpoint/{projectApiKey}")
    public Mono<ResponseEntity<String>> generateEndpoint(Authentication authentication, @PathVariable String projectApiKey, @RequestBody String description) {
        return aiService.generateEndpointFromPrompt(authentication.getName(), projectApiKey, description)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).body("Error: " + ex.getMessage()));
                });
    }

    @PostMapping("/createModel/{projectApiKey}")
    public ResponseEntity<Map<String, String>> createModel(Authentication authentication, @PathVariable String projectApiKey, @RequestBody ModelCommands.CreateModelCommand command) {
        aiService.createModel(authentication.getName(), projectApiKey, command);
        Map<String, String> response = Map.of("message", "Model created successfully");
        return ResponseEntity.ok(response);
    }
}
