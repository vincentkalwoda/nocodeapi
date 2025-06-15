package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiAccessToken;
import at.kalwoda.nocodeapi.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiAccessTokenRepository extends JpaRepository<ApiAccessToken, ApiKey> {
    Optional<ApiAccessToken> findByApiKey(ApiKey apiKey);
    Optional<ApiAccessToken> findByProjectApiKey(ApiKey projectApiKey);
}
