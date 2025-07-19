package at.kalwoda.nocodeapi.persistance;

import at.kalwoda.nocodeapi.FixturesFactory;
import at.kalwoda.nocodeapi.TestcontainersConfiguration;
import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.domain.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import({TestcontainersConfiguration.class})
class EntityModelRepositoryTest {

    private @Autowired EntityModelRepository entityModelRepository;
    private @Autowired ProjectRepository projectRepository;
    private EntityModel entityModel;
    private Project project;

    @BeforeEach
    void setUp() {
        project = FixturesFactory.project();
        projectRepository.saveAndFlush(project);
        entityModel = FixturesFactory.entityModel(project);
        entityModelRepository.saveAndFlush(entityModel);
        assertThat(entityModelRepository).isNotNull();
    }

    @Test
    void can_findByApiKey() {
        var foundEntityModel = entityModelRepository.findByApiKey(entityModel.getApiKey());
        assertThat(foundEntityModel).isNotEmpty();
        assertThat(foundEntityModel.get().getApiKey()).isEqualTo(entityModel.getApiKey());
    }

    @Test
    void can_findByName() {
        var foundEntityModel = entityModelRepository.findByName(entityModel.getName());
        assertThat(foundEntityModel).isNotEmpty();
        assertThat(foundEntityModel.get().getName()).isEqualTo(entityModel.getName());
    }
}