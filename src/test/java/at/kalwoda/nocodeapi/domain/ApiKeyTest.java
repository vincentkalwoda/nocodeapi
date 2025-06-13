package at.kalwoda.nocodeapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiKeyTest {
    @Test
    void valid_ApiKey_ShouldCreateInstance() {
        ApiKey apiKey = new ApiKey("hedahb3h1hbebhbahdw");
        assertEquals("hedahb3h1hbebhbahdw", apiKey.value());
    }

    @Test
    void null_ApiKey_ShouldThrowException() {
        Exception exception = assertThrows(ApiKey.ApiKeyException.class, () -> new ApiKey(null));
        assertEquals("Api key must not be null!", exception.getMessage());
    }

    @Test
    void empty_ApiKey_ShouldThrowException() {
        Exception exception = assertThrows(ApiKey.ApiKeyException.class, () -> new ApiKey(""));
        assertEquals("Api key must not be blank!", exception.getMessage());
    }

}