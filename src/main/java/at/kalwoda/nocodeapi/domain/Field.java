package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Field(
        String name,
        FieldType type,
        Boolean isRequired
) {

    public Field {
        if(name == null || name.isBlank())
            throw FieldException.forNullOrBlankName();
        if(type == null)
            throw FieldException.forInvalidType();
        if(isRequired == null)
            isRequired = false;
    }

    static class FieldException extends RuntimeException {
        private FieldException(String message) {
            super(message);
        }

        static FieldException forNullOrBlankName() {
            return new FieldException("Field name must not be null or blank!");
        }

        static FieldException forInvalidType() {
            return new FieldException("Field type must not be null!");
        }
    }
}
