package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.PromptHistoryService;
import at.kalwoda.nocodeapi.service.dtos.prompthistory.PromptHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiConstants.API + "/promptHistory")
public class PromptHistoryRestController {
    private final PromptHistoryService promptHistoryService;

    @GetMapping("/getPromptHistory/{projectApiKey}")
    public ResponseEntity<List<PromptHistoryDto>> getPromptHistory(Authentication authentication, @PathVariable String projectApiKey) {
        List<PromptHistoryDto> history = promptHistoryService.getPromptHistory(authentication.getName(), projectApiKey);
        return ResponseEntity.ok(history);
    }
}
