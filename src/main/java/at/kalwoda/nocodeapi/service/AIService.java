package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.domain.User;
import at.kalwoda.nocodeapi.persistance.ProjectRepository;
import at.kalwoda.nocodeapi.service.commands.EntityCommands;
import at.kalwoda.nocodeapi.service.commands.FieldCommands;
import at.kalwoda.nocodeapi.service.commands.ModelCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AIService {

    private final LocalContainerEntityManagerFactoryBean entityManagerFactory2;
    private final ProjectService projectService;
    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;
    private final EntityService entityService;
    private final FieldService fieldService;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    public Mono<String> generateModelFromPrompt(String username, String projectApiKey, String userPrompt) {
        var body = Map.of(
                "model", "nousresearch/hermes-2-pro-llama-3-8b",
                "messages", new Object[]{
                        Map.of("role", "system", "content", """
                                You are a backend schema generator. Your task is to convert a user's request for data models into a valid JSON format.
                                
                                The JSON must follow this structure:
                                
                                {
                                  "entities": [
                                    {
                                      "name": "entity_name",
                                      "fields": [
                                        {
                                          "name": "field_name",
                                          "type": "STRING | INTEGER | FLOAT | BOOLEAN | DATE",
                                          "constraints": {
                                             "NOT_NULL": "",
                                             "UNIQUE": "",
                                             "PRIMARY_KEY": "",
                                             "DEFAULT": "default_value",
                                             "REGEX": "regex_pattern",
                                             "MIN": "minimum_value",
                                             "MAX": "maximum_value",
                                             "MIN_LENGTH": "min_length",
                                             "MAX_LENGTH": "max_length",
                                             "FOREIGN_KEY": {
                                               "target_entity": "target_entity_name",
                                               "target_field": "target_field_name",
                                               "relation_type": "ONE_TO_ONE | ONE_TO_MANY | MANY_TO_ONE | MANY_TO_MANY"
                                             }
                                           }
                                        }
                                      ]
                                    }
                                  ]
                                }
                                
                                IMPORTANT: When using REGEX constraints, you MUST properly escape special characters for JSON:
                                - Use \\\\ instead of \\
                                - Use \\\\d instead of \\d
                                - Use \\\\w instead of \\w
                                - Use \\\\. instead of \\.
                                - Use \\\\/ instead of \\/
                                
                                Examples of properly escaped regex patterns:
                                - Email: "^[\\\\w.-]+@[\\\\w-]+\\\\.[a-zA-Z]{2,}$"
                                - Phone: "^\\\\+?\\\\d{9,15}$"
                                - URL: "^https?:\\\\/\\\\/[\\\\w.-]+\\\\.[a-zA-Z]{2,}.*$"
                                
                                Respond only with the JSON. Do not add any explanation or notes.
                                
                                🧪 Example:
                                
                                User: "I want a table of users with username, email, and age. Username and email must not be null and unique. Email must match a pattern. Age must be a number greater than 13."
                                
                                Output:
                                
                                {
                                  "entities": [
                                    {
                                      "name": "users",
                                      "fields": [
                                        {
                                          "name": "id",
                                          "type": "INTEGER",
                                            "constraints": {
                                                "NOT_NULL": "",
                                                "PRIMARY_KEY": "",
                                                "DEFAULT": "nextval('users_id_seq'::regclass)"
                                        },
                                        {
                                          "name": "username",
                                          "type": "STRING",
                                          "constraints": {
                                            "NOT_NULL": "",
                                            "UNIQUE": ""
                                          }
                                        },
                                        {
                                          "name": "email",
                                          "type": "STRING",
                                          "constraints": {
                                            "NOT_NULL": "",
                                            "UNIQUE": "",
                                            "REGEX": "^[\\\\w.-]+@[\\\\w-]+\\\\.[a-zA-Z]{2,}$"
                                          }
                                        },
                                        {
                                          "name": "age",
                                          "type": "INTEGER",
                                          "constraints": {
                                            "MIN": "13"
                                          }
                                        }
                                      ]
                                    }
                                  ]
                                }
                                
                                Now, generate the JSON schema for this user request:
                                """),
                        Map.of("role", "user", "content", userPrompt)
                }
        );

        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        return webClientBuilder
                .baseUrl("https://openrouter.ai/api/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .build()
                .post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Speichern
                    project.setPromptText(content);
                    projectRepository.save(project);

                    return content;
                });
    }

    public void createModel(String username, String projectApiKey, ModelCommands.CreateModelCommand command) {
        for (ModelCommands.CreateEntityModelCommand entity : command.entities()) {
            entityService.createEntity(username, projectApiKey, new EntityCommands.CreateEntityCommand(entity.name()));
            for (FieldCommands.CreateFieldCommand field : entity.fields()) {
                EntityModel entityModel = entityService.getEntityByName(username, projectApiKey, entity.name());
                fieldService.createField(username, entityModel.getApiKey().value(), new FieldCommands.CreateFieldCommand(
                        field.name(),
                        field.type(),
                        field.constraints() == null ? Map.of() : field.constraints()
                ));
            }
        }
    }

    public Mono<String> generateEndpointFromPrompt(String username, String projectApiKey, String userPrompt) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        var body = Map.of(
                "model", "nousresearch/hermes-2-pro-llama-3-8b",
                "messages", new Object[]{
                        Map.of("role", "system", "content", """
                                You are an expert backend engineer.
                                Your job is to translate user requirements into REST API endpoint definitions.
                                
                                You receive natural language descriptions from a user and return one or more endpoint definitions as JSON.
                                Each endpoint represents a use case (e.g. "Get all users with active orders", or "Update all products that are out of stock").
                                
                                Your JSON output must follow this exact structure:
                                {
                                  "endpoints": [
                                    {
                                      "method": "GET | POST | PUT | DELETE",
                                      "path": "/custom-endpoint-path",
                                      "description": "What the endpoint does in one sentence",
                                      "query_logic": "SQL",
                                      "required_parameters": ["param1", "param2 (optional)", "..."],
                                      "filters": ["optional_field", "..."],
                                      "response_example": {
                                        "example_field": "example_value"
                                      }
                                    }
                                  ]
                                }
                                
                                Rules:
                                
                                Always generate at least one endpoint if the request is valid.
                                
                                Use RESTful naming conventions (snake_case or kebab-case for paths).
                                
                                If unsure about parameters, infer common ones.
                                
                                Don’t make assumptions about authentication, unless specified.
                                
                                Only generate JSON — no text, no markdown, no explanations.
                                
                                Here's the schema from the model:
                                """ + project.getPromptText()),
                        Map.of("role", "user", "content", userPrompt)
                }
        );

        return webClientBuilder
                .baseUrl("https://openrouter.ai/api/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .build()
                .post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (List<Map<String, Object>>) response.get("choices");
                    var message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Speichern
                    project.setPromptText(content);
                    projectRepository.save(project);

                    return content;
                });
    }

}
