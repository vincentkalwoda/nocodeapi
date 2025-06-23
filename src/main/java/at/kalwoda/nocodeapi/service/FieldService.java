package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.EntityModelRepository;
import at.kalwoda.nocodeapi.persistance.FieldRepository;
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

    public List<Field> getFields(String username, String entityApiKey) {
        EntityModel entityModel = entityRepository.findByApiKey(new ApiKey(entityApiKey))
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));

        if (!entityModel.getProject().getUser().getUsername().equals(username)) {
            throw new NoSuchElementException("Entity not found");
        }

        return fieldRepository.findAllByEntity_ApiKey(entityModel.getApiKey());
    }

    public Field getField(String username, String fieldApiKey) {
        Field field = fieldRepository.findByApiKey(new ApiKey(fieldApiKey))
                .orElseThrow(() -> new NoSuchElementException("Field not found"));

        if (!field.getEntity().getProject().getUser().getUsername().equals(username)) {
            throw new NoSuchElementException("Entity not found");
        }

        return fieldRepository.findByApiKey(new ApiKey(fieldApiKey))
                .orElseThrow(() -> new NoSuchElementException("Field not found"));
    }

    public Field createField(String username, String entityApiKey, @Valid CreateFieldCommand command) {
        EntityModel entityModel = entityRepository.findByApiKey(new ApiKey(entityApiKey))
                .orElseThrow(() -> new NoSuchElementException("Entity not found"));

        if (!entityModel.getProject().getUser().getUsername().equals(username)) {
            throw new NoSuchElementException("Entity not found");
        }

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("f_" + Base58.random(16));
        } while (fieldRepository.findByApiKey(apiKey).isPresent());

        Field field = Field.builder()
                .apiKey(new ApiKey("f_" + Base58.random(16)))
                .name(command.name())
                .type(FieldType.valueOf(command.type().toUpperCase()))
                .isRequired(command.required())
                .relationTarget(new ApiKey(command.relationTargetApiKey()))
                .relationType(RelationshipType.valueOf(command.relationType().toUpperCase()))
                .entity(entityModel)
                .build();

        return fieldRepository.save(field);
    }
}
