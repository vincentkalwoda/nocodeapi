package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.domain.PromptHistory;
import at.kalwoda.nocodeapi.domain.Role;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.PromptHistoryRepository;
import at.kalwoda.nocodeapi.service.dtos.prompthistory.PromptHistoryDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
public class PromptHistoryService {

    private final PromptHistoryRepository promptHistoryRepository;
    private final ProjectService projectService;

    public List<PromptHistoryDto> getPromptHistory(String username, String projectApiKey) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        return promptHistoryRepository.findByProject_ApiKey(project.getApiKey())
                .stream()
                .sorted(Comparator.comparing(PromptHistory::getCreatedAt))
                .map(PromptHistoryDto::new)
                .collect(Collectors.toList());
    }

    public List<PromptHistoryDto> createPromptHistory(String username, String projectApiKey, Role role, String content) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("ph_" + Base58.random(16));
        } while (promptHistoryRepository.findByApiKey(apiKey).isPresent());

        PromptHistory promptHistory = PromptHistory.builder()
                .apiKey(apiKey)
                .project(project)
                .role(role)
                .content(content)
                .createdAt(new java.util.Date())
                .build();

        promptHistoryRepository.save(promptHistory);

        return getPromptHistory(username, projectApiKey);
    }
}
