package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.EntityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityModelRepository extends JpaRepository<EntityModel, ApiKey> {
    Optional<EntityModel> findByApiKey(ApiKey apiKey);
    Optional<EntityModel> findByName(String name);

    List<EntityModel> findByProjectApiKey(ApiKey projectApiKey);
}
