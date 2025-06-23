package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FieldRepository extends JpaRepository<Field, ApiKey> {
    Optional<Field> findByApiKey(ApiKey apiKey);
    Optional<Field> findByName(String name);

    List<Field> findAllByEntity_ApiKey(ApiKey entityApiKey);
}
