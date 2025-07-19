package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromptHistoryRepository extends JpaRepository<PromptHistory, ApiKey> {
    Optional<PromptHistory> findByApiKey(ApiKey apiKey);

    List<PromptHistory> findByProject_ApiKey(ApiKey apiKey);
}
