package at.kalwoda.nocodeapi.service.dtos.request;

import at.kalwoda.nocodeapi.domain.MethodTypes;
import at.kalwoda.nocodeapi.domain.Request;

import java.sql.Time;
import java.util.Date;

public record RequestDto(
        String apiKey,
        String path,
        MethodTypes method,
        String body,
        String queryParams,
        String headers,
        String response,
        int statusCode,
        String errorMessage,
        Date createdAt,
        Long responseTime,
        String userAgent,
        String ipAddress
) {
    public RequestDto(Request request) {
        this(
                request.getApiKey().value(),
                request.getPath(),
                request.getMethod(),
                request.getBody(),
                request.getQueryParams(),
                request.getHeaders(),
                request.getResponse(),
                request.getStatusCode(),
                request.getErrorMessage(),
                request.getCreatedAt(),
                request.getResponseTime(),
                request.getUserAgent(),
                request.getIpAddress()
        );
    }
}
