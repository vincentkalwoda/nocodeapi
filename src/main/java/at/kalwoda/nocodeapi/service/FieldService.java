package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.EntityModelRepository;
import at.kalwoda.nocodeapi.persistance.FieldRepository;
import at.kalwoda.nocodeapi.service.commands.FieldCommands;
import at.kalwoda.nocodeapi.service.commands.FieldCommands.CreateFieldCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
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

        // Build constraints list
        List<ConstraintDefinition> constraints = buildConstraints(command, entityModel);

        Field field = Field.builder()
                .apiKey(apiKey)
                .name(command.name())
                .type(FieldType.valueOf(command.type().toUpperCase()))
                .constraints(constraints)
                .entity(entityModel)
                .build();

        return fieldRepository.save(field);
    }

    private List<ConstraintDefinition> buildConstraints(CreateFieldCommand command, EntityModel entityModel) {
        List<ConstraintDefinition> constraints = new ArrayList<>();

        // Handle all constraints from the constraints map
        if (command.constraints() != null && !command.constraints().isEmpty()) {
            for (Map.Entry<String, Object> entry : command.constraints().entrySet()) {
                String constraintType = entry.getKey().toUpperCase();
                Object constraintValue = entry.getValue();

                try {
                    Constraints constraint = Constraints.valueOf(constraintType);

                    if(constraint.equals(Constraints.FOREIGN_KEY)) {
                        ForeignKeyMetadata fkmd = convertToForeignKeyMetadata(constraintValue);
                        validateRelationTarget(entityModel, fkmd.targetEntity(), fkmd.targetField());
                        constraints.add(new ConstraintDefinition(constraint, null, fkmd));
                    } else {
                        validateConstraint(command.type(), constraint, constraintValue.toString());

                        constraints.add(new ConstraintDefinition(constraint, constraintValue.toString(), null));
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid constraint type: {}", constraintType);
                    throw new IllegalArgumentException("Invalid constraint type: " + constraintType);
                }
            }
        }
        return constraints;
    }

    private void validateRelationTarget(EntityModel e, String targetEntityName, String targetFieldName) {
        EntityModel targetEntity = entityRepository.findByProjectAndName(e.getProject(), targetEntityName)
                .orElseThrow(() -> new NoSuchElementException("Target entity not found: " + targetEntityName));

        if(fieldRepository.findByNameAndEntity(targetFieldName, targetEntity).isEmpty()) {
            throw new NoSuchElementException("Target field not found in target entity: " + targetFieldName);
        }
    }

    private void validateConstraint(String fieldType, Constraints constraint, String value) {
        FieldType type = FieldType.valueOf(fieldType.toUpperCase());

        switch (constraint) {
            case MIN_LENGTH, MAX_LENGTH -> {
                if (type != FieldType.STRING) {
                    throw new IllegalArgumentException("Length constraints can only be applied to STRING fields");
                }
                validateNumericValue(value, constraint.name());
            }
            case MIN, MAX -> {
                if (type != FieldType.INTEGER && type != FieldType.FLOAT) {
                    throw new IllegalArgumentException("MIN/MAX constraints can only be applied to INTEGER or FLOAT fields");
                }
                validateNumericValue(value, constraint.name());
            }
            case REGEX -> {
                if (type != FieldType.STRING) {
                    throw new IllegalArgumentException("REGEX constraint can only be applied to STRING fields");
                }
                validateRegexPattern(value);
            }
            case DEFAULT -> {
                validateDefaultValue(type, value);
            }
            case UNIQUE, NOT_NULL -> {
                // These constraints are valid for all field types
                // NOT_NULL has no value, others might need empty string as value
                if (constraint == Constraints.NOT_NULL && value != null && !value.isEmpty()) {
                    log.warn("NOT_NULL constraint should not have a value, ignoring value: {}", value);
                }
            }
            case FOREIGN_KEY -> {
                // Already validated in validateRelationTarget
            }
            case PRIMARY_KEY -> {

            }
        }
    }

    private void validateNumericValue(String value, String constraintName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(constraintName + " constraint requires a numeric value");
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(constraintName + " constraint value must be numeric: " + value);
        }
    }

    private void validateRegexPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("REGEX constraint requires a pattern");
        }
        try {
            java.util.regex.Pattern.compile(pattern);
        } catch (java.util.regex.PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + pattern);
        }
    }

    private void validateDefaultValue(FieldType type, String value) {
        if (value == null) {
            return; // null default is allowed
        }

        try {
            switch (type) {
                case INTEGER -> Integer.parseInt(value);
                case FLOAT -> Double.parseDouble(value);
                case BOOLEAN -> {
                    if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                        throw new IllegalArgumentException("Boolean default value must be 'true' or 'false'");
                    }
                }
                case DATE -> {
                    // Basic date format validation - you might want to use a proper date parser
                    if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        throw new IllegalArgumentException("Date default value must be in YYYY-MM-DD format");
                    }
                }
                case STRING -> {
                    // String values are always valid
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid default value for " + type + " field: " + value);
        }
    }

    public Field updateField(String username, String apiKey, @Valid FieldCommands.UpdateFieldCommand command) {
        Field field = getField(username, apiKey);

        projectService.checkProjectOwnership(username, field.getEntity().getProject().getApiKey().value());

        command.name().ifPresent(field::setName);
        command.type().ifPresent(type -> field.setType(FieldType.valueOf(type.toUpperCase())));

        return fieldRepository.save(field);
    }

    public void deleteField(String username, String apiKey) {
        Field field = getField(username, apiKey);

        projectService.checkProjectOwnership(username, field.getEntity().getProject().getApiKey().value());

        fieldRepository.delete(field);
    }

    private ForeignKeyMetadata convertToForeignKeyMetadata(Object value) {
        if (value instanceof Map<?, ?> map) {
            String targetEntity = (String) map.get("target_entity");
            String targetField = (String) map.get("target_field");
            RelationshipType relationType = RelationshipType.valueOf(((String) map.get("relation_type")).toUpperCase());
            return new ForeignKeyMetadata(targetEntity, targetField, relationType);
        }
        throw new IllegalArgumentException("Invalid FOREIGN_KEY constraint format");
    }

}