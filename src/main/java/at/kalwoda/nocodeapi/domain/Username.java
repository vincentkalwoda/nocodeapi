package at.kalwoda.nocodeapi.domain;

public record Username(String value) {

    public Username {
        if (value == null || value.isBlank())
            throw UsernameException.forNullOrBlankValue();
        if (value.length() < 3 || value.length() > 20)
            throw UsernameException.forInvalidLength();
        if (!value.matches("^[a-zA-Z0-9_]+$"))
            throw UsernameException.forInvalidCharacters();
    }

    static class UsernameException extends RuntimeException {
        private UsernameException(String message) {
            super(message);
        }

        static UsernameException forNullOrBlankValue() {
            return new UsernameException("Username must not be null or blank!");
        }

        static UsernameException forInvalidLength() {
            return new UsernameException("Username must be between 3 and 20 characters long!");
        }

        static UsernameException forInvalidCharacters() {
            return new UsernameException("Username can only contain alphanumeric characters and underscores!");
        }
    }
}
