package at.kalwoda.nocodeapi.domain;

public record ApiKey(String value) {
    public ApiKey {
        if (value == null) throw ApiKeyException.forNullValue();
        if (value.isBlank()) throw ApiKeyException.forBlankValue();
    }

    public static class ApiKeyException extends RuntimeException {
        private ApiKeyException(String message) {
            super(message);
        }

        static ApiKeyException forNullValue() {
            return new ApiKeyException("Api key must not be null!");
        }

        static ApiKeyException forBlankValue() {
            return new ApiKeyException("Api key must not be blank!");
        }

    }
}