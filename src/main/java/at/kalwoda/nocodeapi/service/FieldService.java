package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.EntityModelRepository;
import at.kalwoda.nocodeapi.persistance.FieldRepository;
import at.kalwoda.nocodeapi.service.commands.FieldCommands;
import at.kalwoda.nocodeapi.service.commands.FieldCommands.CreateFieldCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class FieldService {

    public final FieldRepository fieldRepository;
    public final EntityModelRepository entityRepository;
    public final ProjectService projectService;

    public List<Field> getFields(String username, String entityApiKey) {
        EntityModel entityModel = entityRepository.findByApiKey(new ApiKey(entityApiKey))
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));

        projectService.checkProjectOwnership(username, entityModel.getProject().getApiKey().value());

        return fieldRepository.findAllByEntity_ApiKey(entityModel.getApiKey());
    }

    public Field getField(String username, String fieldApiKey) {
        Field field = fieldRepository.findByApiKey(new ApiKey(fieldApiKey))
                .orElseThrow(() -> new NoSuchElementException("Field not found"));

        projectService.checkProjectOwnership(username, field.getEntity().getProject().getApiKey().value());

        return field;
    }

    public Field createField(String username, String entityApiKey, @Valid CreateFieldCommand command) {
        EntityModel entityModel = entityRepository.findByApiKey(new ApiKey(entityApiKey))
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));

        projectService.checkProjectOwnership(username, entityModel.getProject().getApiKey().value());

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("f_" + Base58.random(16));
        } while (fieldRepository.findByApiKey(apiKey).isPresent());

        Field field = Field.builder()
                .apiKey(apiKey)
                .name(command.name())
                .type(FieldType.valueOf(command.type().toUpperCase()))
                .isRequired(command.required())
                .relationTarget(command.relationTargetApiKey() != null ? new ApiKey(command.relationTargetApiKey()) : null)
                .relationType(command.relationType() != null ? RelationshipType.valueOf(command.relationType().toUpperCase()) : null)
                .entity(entityModel)
                .build();

        return fieldRepository.save(field);
    }

    public Field updateField(String username, String apiKey, @Valid FieldCommands.UpdateFieldCommand command) {
        Field field = getField(username, apiKey);

        projectService.checkProjectOwnership(username, field.getEntity().getProject().getApiKey().value());

        command.name().ifPresent(field::setName);
        command.type().ifPresent(type -> field.setType(FieldType.valueOf(type.toUpperCase())));
        command.required().ifPresent(field::setIsRequired);
        command.relationTargetApiKey().ifPresent(relationTarget ->
                field.setRelationTarget(new ApiKey(relationTarget)));
        command.relationType().ifPresent(relationType ->
                field.setRelationType(RelationshipType.valueOf(relationType.toUpperCase())));

        return fieldRepository.save(field);
    }

    public void deleteField(String username, String apiKey) {
        Field field = getField(username, apiKey);

        projectService.checkProjectOwnership(username, field.getEntity().getProject().getApiKey().value());

        fieldRepository.delete(field);
    }
}
