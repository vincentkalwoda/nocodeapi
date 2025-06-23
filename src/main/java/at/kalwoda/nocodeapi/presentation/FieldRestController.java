package at.kalwoda.nocodeapi.presentation;

import at.kalwoda.nocodeapi.service.FieldService;
import at.kalwoda.nocodeapi.service.commands.FieldCommands;
import at.kalwoda.nocodeapi.service.commands.FieldCommands.CreateFieldCommand;
import at.kalwoda.nocodeapi.service.dtos.field.FieldDto;
import at.kalwoda.nocodeapi.service.dtos.field.FieldMinimalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping(ApiConstants.API + "/fields")
public class FieldRestController {

    public final FieldService fieldService;

    @GetMapping("/getFields/{entityApiKey}")
    public ResponseEntity<List<FieldDto>> getFields(Authentication authentication, @PathVariable String entityApiKey) {
        List<FieldDto> fields = fieldService.getFields(authentication.getName(), entityApiKey)
                .stream()
                .map(FieldDto::new)
                .toList();

        return ResponseEntity.ok(fields);
    }

    @GetMapping("/getFields/{entityApiKey}/minimal")
    public ResponseEntity<List<FieldMinimalDto>> getFieldsMinimal(Authentication authentication, @PathVariable String entityApiKey) {
        List<FieldMinimalDto> fields = fieldService.getFields(authentication.getName(), entityApiKey)
                .stream()
                .map(FieldMinimalDto::new)
                .toList();

        return ResponseEntity.ok(fields);
    }

    @GetMapping("/getField/{fieldApiKey}")
    public ResponseEntity<FieldDto> getField(Authentication authentication, @PathVariable String fieldApiKey) {
        FieldDto field = new FieldDto(fieldService.getField(authentication.getName(), fieldApiKey));
        return ResponseEntity.ok(field);
    }

    @GetMapping("/getField/{fieldApiKey}/minimal")
    public ResponseEntity<FieldMinimalDto> getFieldMinimal(Authentication authentication, @PathVariable String fieldApiKey) {
        FieldMinimalDto field = new FieldMinimalDto(fieldService.getField(authentication.getName(), fieldApiKey));
        return ResponseEntity.ok(field);
    }

    @PostMapping("/createField/{entityApiKey}")
    public ResponseEntity<FieldDto> createField(Authentication authentication, @PathVariable String entityApiKey, @RequestBody CreateFieldCommand command) {
        FieldDto createdField = new FieldDto(fieldService.createField(authentication.getName(), entityApiKey, command));
        return ResponseEntity.status(201).body(createdField);
    }

    @PatchMapping("/updateField/{fieldApiKey}")
    public ResponseEntity<FieldDto> updateField(Authentication authentication, @PathVariable String fieldApiKey, @RequestBody FieldCommands.UpdateFieldCommand command) {
        FieldDto field = new FieldDto(fieldService.updateField(authentication.getName(), fieldApiKey, command));
        return ResponseEntity.ok(field);
    }

    @DeleteMapping("/deleteField/{fieldApiKey}")
    public ResponseEntity<Void> deleteField(Authentication authentication, @PathVariable String fieldApiKey) {
        fieldService.deleteField(authentication.getName(), fieldApiKey);
        return ResponseEntity.noContent().build();
    }
}
