package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Email(String value) {
    public Email {
        if (value == null)
            throw EmailException.forNullValue();
        if (!value.matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$"))
            throw EmailException.forInvalidFormat();
    }

    public static class EmailException extends RuntimeException {
        private EmailException(String message) {
            super(message);
        }

        static EmailException forNullValue() {
            return new Email.EmailException("Email must not be null!");
        }

        static Email.EmailException forInvalidFormat() {
            return new Email.EmailException("Email is invalid!");
        }
    }
}
