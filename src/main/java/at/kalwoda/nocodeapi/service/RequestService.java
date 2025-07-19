package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.domain.Request;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.RequestRepository;
import at.kalwoda.nocodeapi.service.commands.RequestCommands;
import at.kalwoda.nocodeapi.service.commands.RequestCommands.CreateRequestCommand;
import at.kalwoda.nocodeapi.service.dtos.request.RequestStatsDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
public class RequestService {
    private final ProjectService projectService;
    private final RequestRepository requestRepository;

    public Request getRequest(String username, String apiKey) {
        Request request = requestRepository.findByApiKey(new ApiKey(apiKey))
                .orElseThrow(() -> new NoSuchElementException("Request not found"));

        projectService.checkProjectOwnership(username, request.getProject().getApiKey().value());

        return request;
    }

    public List<Request> getRequestsByProject(String username, String projectApiKey) {
        projectService.checkProjectOwnership(username, projectApiKey);

        return requestRepository.findByProject_ApiKey(new ApiKey(projectApiKey));
    }

    public RequestStatsDto getRequestStats(String username, String projectApiKey) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        if(project.getRequests().isEmpty()) {
            return new RequestStatsDto(0, 0.0, 0.0, 1.0);
        }
        return requestRepository.getRequestStats(project.getApiKey());
    }

    public Request createRequest(String username, String projectApiKey, @Valid CreateRequestCommand command) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("r_" + Base58.random(16));
        } while (requestRepository.findByApiKey(apiKey).isPresent());

        Request request = Request.builder()
                .apiKey(apiKey)
                .path(command.path())
                .method(command.method())
                .body(command.body())
                .headers(command.headers())
                .queryParams(command.queryParams())
                .response(command.response())
                .errorMessage(command.errorMessage())
                .statusCode(command.statusCode())
                .responseTime(command.responseTime())
                .createdAt(new Date())
                .userAgent(command.userAgent())
                .ipAddress(command.ipAddress())
                .project(project)
                .build();

        return requestRepository.save(request);
    }
}
