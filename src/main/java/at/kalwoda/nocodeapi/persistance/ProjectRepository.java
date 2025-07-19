package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, ApiKey> {
    Optional<Project> findByApiKey(ApiKey apiKey);

    Optional<Project> findByName(String name);

    Optional<Project> findByUserAndApiKey(User user, ApiKey apiKey);
}
