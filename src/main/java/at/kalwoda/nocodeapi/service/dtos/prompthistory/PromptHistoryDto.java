package at.kalwoda.nocodeapi.service.dtos.prompthistory;

import at.kalwoda.nocodeapi.domain.PromptHistory;
import at.kalwoda.nocodeapi.domain.Role;

import java.util.Date;

public record PromptHistoryDto(
        String apiKey,
        Role role,
        String content,
        Date created_at
) {
    public PromptHistoryDto(PromptHistory promptHistory) {
        this(
                promptHistory.getApiKey().value(),
                promptHistory.getRole(),
                promptHistory.getContent(),
                promptHistory.getCreatedAt()
        );
    }
}
