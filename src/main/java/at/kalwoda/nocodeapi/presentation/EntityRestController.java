package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.EntityService;
import at.kalwoda.nocodeapi.service.commands.EntityCommands;
import at.kalwoda.nocodeapi.service.dtos.entity.EntityDto;
import at.kalwoda.nocodeapi.service.dtos.entity.EntityMinimalDto;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API + "/entities")
public class EntityRestController {

    private final EntityService entityService;

    @GetMapping("/getEntities/{projectApiKey}")
    public ResponseEntity<List<EntityDto>> getEntities(Authentication authentication, @PathVariable String projectApiKey) {
        List<EntityDto> entity = entityService.getEntities(authentication.getName(), projectApiKey)
                .stream()
                .map(EntityDto::new)
                .toList();
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/getEntities/{projectApiKey}/minimal")
    public ResponseEntity<List<EntityMinimalDto>> getEntitiesMinimal(Authentication authentication, @PathVariable String projectApiKey) {
        List<EntityMinimalDto> entity = entityService.getEntities(authentication.getName(), projectApiKey)
                .stream()
                .map(EntityMinimalDto::new)
                .toList();
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/getEntity/{entityApiKey}")
    public ResponseEntity<EntityDto> getEntity(Authentication authentication, @PathVariable String entityApiKey) {
        EntityDto entity = new EntityDto(entityService.getEntity(authentication.getName(), entityApiKey));
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/getEntity/{entityApiKey}/minimal")
    public ResponseEntity<EntityMinimalDto> getEntityMinimal(Authentication authentication, @PathVariable String entityApiKey) {
        EntityMinimalDto entity = new EntityMinimalDto(entityService.getEntity(authentication.getName(), entityApiKey));
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/createEntity/{projectApiKey}")
    public ResponseEntity<EntityDto> createEntity(Authentication authentication, @PathVariable String projectApiKey,
                                                  @RequestBody @Valid EntityCommands.CreateEntityCommand command) {
        EntityDto entity = new EntityDto(entityService.createEntity(authentication.getName(), projectApiKey, command));
        return ResponseEntity.status(201).body(entity);
    }
}
