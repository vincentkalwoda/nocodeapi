package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Email;
import at.kalwoda.nocodeapi.domain.User;
import at.kalwoda.nocodeapi.domain.Username;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, ApiKey> {
    Optional<User> findByApiKey(ApiKey apiKey);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(Username username);
}
