package at.kalwoda.nocodeapi.service.dtos.request;

public record RequestStatsDto(
        long totalRequests,
        double averageResponseTime,
        double successRate,
        double apiVersion
) {
    public RequestStatsDto(long totalRequests, double averageResponseTime, double successRate, double apiVersion) {
        this.totalRequests = totalRequests;
        this.averageResponseTime = averageResponseTime;
        this.successRate = successRate;
        this.apiVersion = apiVersion;
    }
}
