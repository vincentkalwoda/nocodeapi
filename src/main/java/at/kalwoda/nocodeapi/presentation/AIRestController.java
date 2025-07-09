package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.AIService;
import at.kalwoda.nocodeapi.service.commands.ModelCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiConstants.API + "/ai")
@RequiredArgsConstructor
public class AIRestController {
    private final AIService aiService;

    @PostMapping("/generateModel/{projectApiKey}")
    public Mono<ResponseEntity<String>> generate(Authentication authentication, @PathVariable String projectApiKey, @RequestBody String description) {
        return aiService.generateModelFromPrompt(authentication.getName(), projectApiKey, description)
                .map(ResponseEntity::ok)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return Mono.just(ResponseEntity.status(500).body("Error: " + ex.getMessage()));
                });
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
    public ResponseEntity<String> createModel(Authentication authentication, @PathVariable String projectApiKey, @RequestBody ModelCommands.CreateModelCommand command) {
        aiService.createModel(authentication.getName(), projectApiKey, command);
        return ResponseEntity.ok("Model created successfully");
    }
}
