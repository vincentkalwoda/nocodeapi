package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.FixturesFactory;
import at.kalwoda.nocodeapi.TestcontainersConfiguration;
import at.kalwoda.nocodeapi.domain.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TestcontainersConfiguration.class})
class ProjectRepositoryTest {

    private @Autowired ProjectRepository projectRepository;
    private Project project;

    @BeforeEach
    void setUp() {
        project = FixturesFactory.project();
        projectRepository.saveAndFlush(project);

        assertNotNull(projectRepository);
    }

    @Test
    void can_save() {
        assertNotNull(projectRepository.saveAndFlush(project).getApiKey());
    }

    @Test
    void can_findByApiKey() {
        var foundProject = projectRepository.findByApiKey(project.getApiKey());
        assertThat(foundProject).isNotEmpty();
        assertThat(foundProject.get().getApiKey()).isEqualTo(project.getApiKey());
    }

    @Test
    void can_findByName() {
        var foundProject = projectRepository.findByName(project.getName());
        assertThat(foundProject).isNotEmpty();
        assertThat(foundProject.get().getName()).isEqualTo(project.getName());
    }
}