package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.RequestService;
import at.kalwoda.nocodeapi.service.dtos.request.RequestDto;
import at.kalwoda.nocodeapi.service.dtos.request.RequestStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API + "/requests")
public class RequestRestController {

    private final RequestService requestService;

    @GetMapping("/getRequest/{apiKey}")
    public ResponseEntity<RequestDto> getRequest(Authentication authentication, @PathVariable String apiKey) {
        return ResponseEntity.ok(new RequestDto(requestService.getRequest(authentication.getName(), apiKey)));
    }

    @GetMapping("/getRequestsByProject/{projectApiKey}")
    public ResponseEntity<List<RequestDto>> getRequestsByProject(Authentication authentication, @PathVariable String projectApiKey) {
        return ResponseEntity.ok(
                requestService.getRequestsByProject(authentication.getName(), projectApiKey)
                        .stream()
                        .map(RequestDto::new)
                        .toList()
        );
    }

    @GetMapping("/getRequestStats/{projectApiKey}")
    public ResponseEntity<RequestStatsDto> getRequestStats(Authentication authentication, @PathVariable String projectApiKey) {
        return ResponseEntity.ok(requestService.getRequestStats(authentication.getName(), projectApiKey));
    }

}
