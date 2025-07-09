package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.EntityModelRepository;
import at.kalwoda.nocodeapi.persistance.ProjectRepository;
import at.kalwoda.nocodeapi.service.commands.EntityCommands;
import at.kalwoda.nocodeapi.service.commands.EntityCommands.CreateEntityCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EntityService {
    public final EntityModelRepository entityRepository;
    public final ProjectRepository projectRepository;
    public final ProjectService projectService;

    public List<EntityModel> getEntities(String username, String projectApiKey) {
        Project project = projectRepository.findByApiKey(new ApiKey(projectApiKey))
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        projectService.checkProjectOwnership(username, project.getApiKey().value());

        return entityRepository.findByProjectApiKey(new ApiKey(projectApiKey));
    }

    public EntityModel getEntity(String username, String entityApiKey) {
        EntityModel entity = entityRepository.findByApiKey(new ApiKey(entityApiKey))
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));

        projectService.checkProjectOwnership(username, entity.getProject().getApiKey().value());

        return entity;
    }

    public EntityModel getEntityByName(String username, String projectApiKey, String entityName) {
        Project project = projectRepository.findByApiKey(new ApiKey(projectApiKey))
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        projectService.checkProjectOwnership(username, project.getApiKey().value());

        return entityRepository.findByProjectAndName(project, entityName)
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));
    }

    public EntityModel createEntity(String username, String projectApiKey, @Valid CreateEntityCommand command) {
        Project project = projectRepository.findByApiKey(new ApiKey(projectApiKey))
                .orElseThrow(() -> new NoSuchElementException("Project not found"));

        projectService.checkProjectOwnership(username, project.getApiKey().value());

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("e_" + Base58.random(16));
        } while (entityRepository.findByApiKey(apiKey).isPresent());

        EntityModel entityModel = EntityModel.builder()
                .apiKey(apiKey)
                .name(command.name())
                .project(project)
                .build();

        return entityRepository.save(entityModel);
    }

    public EntityModel updateEntity(String username, String apiKey, @Valid EntityCommands.UpdateEntityCommand command) {
        EntityModel entity = getEntity(username, apiKey);

        projectService.checkProjectOwnership(username, entity.getProject().getApiKey().value());

        command.name().ifPresent(entity::setName);

        return entityRepository.save(entity);
    }

    public void deleteEntity(String username, String apiKey) {
        EntityModel entity = getEntity(username, apiKey);

        projectService.checkProjectOwnership(username, entity.getProject().getApiKey().value());

        entityRepository.delete(entity);
    }
}
