package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.FixturesFactory;
import at.kalwoda.nocodeapi.TestcontainersConfiguration;
import at.kalwoda.nocodeapi.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({TestcontainersConfiguration.class})
class UserRepositoryTest {

    private @Autowired UserRepository userRepository;
    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        user = FixturesFactory.user();
        userRepository.saveAndFlush(user);

        admin = FixturesFactory.admin();
        userRepository.saveAndFlush(admin);
    }

    @Test
    void can_save() {
        assertThat(userRepository.saveAndFlush(user).getApiKey()).isNotNull();
    }

    @Test
    void can_findByApiKey() {
        var foundUser = userRepository.findByApiKey(user.getApiKey());
        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getApiKey()).isEqualTo(user.getApiKey());
    }

    @Test
    void can_findByEmail() {
        var foundUser = userRepository.findByEmail(user.getEmail());
        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void can_findByUsername() {
        var foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser).isNotEmpty();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

}