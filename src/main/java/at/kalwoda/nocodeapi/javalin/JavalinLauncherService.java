package at.kalwoda.nocodeapi.javalin;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.service.ProjectService;
import at.kalwoda.nocodeapi.service.db.DatabaseProvisioningService;
import at.kalwoda.nocodeapi.service.db.DdlGenerator;
import io.javalin.Javalin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Service
public class JavalinLauncherService {

    private final ActiveApiRegistry registry;
    private final ProjectService projectService;
    private final DatabaseProvisioningService dbProvisioningService;
    private final DdlGenerator ddlGenerator;

    public List<Map<String, Object>> setupApi(String username, String projectApiKey) {
        Project project = projectService.checkProjectOwnership(username, projectApiKey);

        String dbName = "project_" + project.getApiKey().value().replace("-", "_");
        String key = registry.getKeyForProject(username, projectApiKey);

        dbProvisioningService.createDatabaseForProject(dbName);

        DataSource projectDataSource = dbProvisioningService.createDataSource(dbName);
        registry.registerDataSource(key, projectDataSource);

        ddlGenerator.executeSchema(projectDataSource, project.getEntities());

        if (project.getEntities() == null || project.getEntities().isEmpty()) {
            throw new IllegalArgumentException("Project has no entities.");
        }

        Optional<Javalin> existingApp = registry.getApp(key);

        if (existingApp.isPresent()) {
            int existingPort = registry.getPort(key).orElse(-1);
            return List.of(Map.of(
                    "status", "already running",
                    "port", existingPort,
                    "url", "http://localhost:" + existingPort
            ));
        }

        int port = registry.reserveNextPort();
        Javalin app = Javalin.create(config -> config.plugins.enableCors(cors -> cors.add(it -> it.anyHost())));

        for (EntityModel entity : project.getEntities()) {
            entity.getFields().size(); // init Lazy
            String route = "/" + entity.getName().toLowerCase(Locale.ROOT);

            // GET all entities
            app.get(route, ctx -> {
                String sql = "SELECT * FROM " + entity.getName().toLowerCase();

                try (Connection conn = projectDataSource.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {

                    List<Map<String, Object>> results = new ArrayList<>();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(meta.getColumnName(i), rs.getObject(i));
                        }
                        results.add(row);
                    }

                    ctx.json(results);
                } catch (SQLException e) {
                    log.error("DB read failed", e);
                    ctx.status(500).json(Map.of("error", "Failed to read data"));
                }
            });

            // POST create entity
            app.post("/" + entity.getName().toLowerCase(), ctx -> {
                Map<String, Object> json = ctx.bodyAsClass(Map.class);

                StringBuilder columns = new StringBuilder();
                StringBuilder placeholders = new StringBuilder();
                List<Object> params = new ArrayList<>();

                // Validate required fields and process field values
                for (Field field : entity.getFields()) {
                    String fieldName = field.getName().toLowerCase();
                    boolean isRequired = isFieldRequired(field);

                    if (json.containsKey(fieldName)) {
                        Object rawValue = json.get(fieldName);

                        // Skip null values for optional fields
                        if (rawValue == null && !isRequired) {
                            continue;
                        }

                        // Validate required field is not null
                        if (rawValue == null && isRequired) {
                            ctx.status(400).json(Map.of("error", "Required field '" + fieldName + "' cannot be null"));
                            return;
                        }

                        Object typedValue;
                        try {
                            typedValue = convertValue(rawValue, field.getType());
                        } catch (Exception e) {
                            ctx.status(400).json(Map.of("error", "Invalid value for field '" + fieldName + "': " + e.getMessage()));
                            return;
                        }

                        // Validate constraints
                        String constraintError = validateFieldConstraints(field, typedValue);
                        if (constraintError != null) {
                            ctx.status(400).json(Map.of("error", constraintError));
                            return;
                        }

                        columns.append(fieldName).append(", ");
                        placeholders.append("?, ");
                        params.add(typedValue);

                    } else if (isRequired) {
                        // Check if field has a DEFAULT constraint
                        boolean hasDefault = field.getConstraints().stream()
                                .anyMatch(c -> c.constraintType() == Constraints.DEFAULT);

                        if (!hasDefault) {
                            ctx.status(400).json(Map.of("error", "Missing required field '" + fieldName + "'"));
                            return;
                        }
                    }
                }

                if (columns.length() == 0) {
                    ctx.status(400).json(Map.of("error", "No valid fields provided."));
                    return;
                }

                columns.setLength(columns.length() - 2);
                placeholders.setLength(placeholders.length() - 2);

                String sql = "INSERT INTO " + entity.getName().toLowerCase() +
                        " (" + columns + ") VALUES (" + placeholders + ")";

                try (Connection conn = projectDataSource.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    pstmt.executeUpdate();
                    ResultSet rs = pstmt.getGeneratedKeys();
                    Map<String, Object> result = new HashMap<>();
                    if (rs.next()) {
                        result.put("id", rs.getObject(1));
                    }
                    result.putAll(json);
                    ctx.status(201).json(result);

                } catch (SQLException e) {
                    log.error("DB insert failed", e);
                    ctx.status(500).json(Map.of("error", "Failed to insert data: " + e.getMessage()));
                }
            });

            // GET entity by ID
            app.get(route + "/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                String sql = "SELECT * FROM " + entity.getName().toLowerCase() + " WHERE id = ?";

                try (Connection conn = projectDataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setLong(1, id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        ResultSetMetaData meta = rs.getMetaData();
                        int columnCount = meta.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(meta.getColumnName(i), rs.getObject(i));
                        }
                        ctx.json(row);
                    } else {
                        ctx.status(404).json(Map.of("error", "Entity not found"));
                    }
                } catch (SQLException e) {
                    log.error("DB fetch by ID failed", e);
                    ctx.status(500).json(Map.of("error", "Failed to fetch data by ID"));
                }
            });

            // PUT update entity by ID
            app.put(route + "/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                Map<String, Object> json = ctx.bodyAsClass(Map.class);

                StringBuilder setClause = new StringBuilder();
                List<Object> params = new ArrayList<>();

                for (Field field : entity.getFields()) {
                    String fieldName = field.getName().toLowerCase();
                    if (json.containsKey(fieldName)) {
                        Object rawValue = json.get(fieldName);

                        if (rawValue == null && isFieldRequired(field)) {
                            ctx.status(400).json(Map.of("error", "Required field '" + fieldName + "' cannot be null"));
                            return;
                        }

                        Object typedValue;
                        try {
                            typedValue = convertValue(rawValue, field.getType());
                        } catch (Exception e) {
                            ctx.status(400).json(Map.of("error", "Invalid value for field '" + fieldName + "': " + e.getMessage()));
                            return;
                        }

                        String constraintError = validateFieldConstraints(field, typedValue);
                        if (constraintError != null) {
                            ctx.status(400).json(Map.of("error", constraintError));
                            return;
                        }

                        setClause.append(fieldName).append(" = ?, ");
                        params.add(typedValue);
                    }
                }

                if (setClause.length() == 0) {
                    ctx.status(400).json(Map.of("error", "No valid fields provided for update."));
                    return;
                }

                setClause.setLength(setClause.length() - 2);
                params.add(id); // Add ID for WHERE clause

                String sql = "UPDATE " + entity.getName().toLowerCase() + " SET " + setClause + " WHERE id = ?";

                try (Connection conn = projectDataSource.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        ctx.json(Map.of("message", "Entity updated successfully", "id", id));
                    } else {
                        ctx.status(404).json(Map.of("error", "Entity not found"));
                    }

                } catch (SQLException e) {
                    log.error("DB update failed", e);
                    ctx.status(500).json(Map.of("error", "Failed to update data: " + e.getMessage()));
                }
            });

            // DELETE entity by ID
            app.delete(route + "/{id}", ctx -> {
                long id = Long.parseLong(ctx.pathParam("id"));
                String sql = "DELETE FROM " + entity.getName().toLowerCase() + " WHERE id = ?";

                try (Connection conn = projectDataSource.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setLong(1, id);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        ctx.json(Map.of("message", "Entity deleted successfully", "id", id));
                    } else {
                        ctx.status(404).json(Map.of("error", "Entity not found"));
                    }

                } catch (SQLException e) {
                    log.error("DB delete failed", e);
                    ctx.status(500).json(Map.of("error", "Failed to delete data: " + e.getMessage()));
                }
            });
        }

        try {
            app.start(port);
            registry.register(key, app, port);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Javalin app: " + e.getMessage(), e);
        }

        List<Map<String, Object>> endpoints = new ArrayList<>();
        for (EntityModel entity : project.getEntities()) {
            endpoints.add(Map.of(
                    "entity", entity.getName(),
                    "url", "http://localhost:" + port + "/" + entity.getName().toLowerCase(Locale.ROOT),
                    "port", port
            ));
        }

        return endpoints;
    }

    /**
     * Check if a field is required by looking for NOT_NULL constraint
     */
    private boolean isFieldRequired(Field field) {
        return field.getConstraints().stream()
                .anyMatch(constraint -> constraint.constraintType() == Constraints.NOT_NULL);
    }

    /**
     * Convert raw value to the appropriate type
     */
    private Object convertValue(Object rawValue, FieldType fieldType) throws Exception {
        if (rawValue == null) {
            return null;
        }

        return switch (fieldType) {
            case STRING -> rawValue.toString();
            case INTEGER -> {
                if (rawValue instanceof Number) {
                    yield ((Number) rawValue).intValue();
                }
                yield Integer.parseInt(rawValue.toString());
            }
            case FLOAT -> {
                if (rawValue instanceof Number) {
                    yield ((Number) rawValue).doubleValue();
                }
                yield Double.parseDouble(rawValue.toString());
            }
            case BOOLEAN -> {
                if (rawValue instanceof Boolean) {
                    yield rawValue;
                }
                yield Boolean.parseBoolean(rawValue.toString());
            }
            case DATE -> {
                if (rawValue instanceof java.sql.Date) {
                    yield rawValue;
                }
                yield java.sql.Date.valueOf(rawValue.toString()); // expects "YYYY-MM-DD"
            }
        };
    }

    /**
     * Validate field constraints
     */
    private String validateFieldConstraints(Field field, Object value) {
        if (value == null) {
            return null; // Null validation is handled separately
        }

        for (ConstraintDefinition constraint : field.getConstraints()) {
            String error = validateSingleConstraint(field, constraint, value);
            if (error != null) {
                return error;
            }
        }
        return null;
    }

    /**
     * Validate a single constraint
     */
    private String validateSingleConstraint(Field field, ConstraintDefinition constraint, Object value) {
        try {
            switch (constraint.constraintType()) {
                case MIN_LENGTH -> {
                    if (field.getType() == FieldType.STRING) {
                        int minLength = Integer.parseInt(constraint.value());
                        if (value.toString().length() < minLength) {
                            return "Field '" + field.getName() + "' must be at least " + minLength + " characters long";
                        }
                    }
                }
                case MAX_LENGTH -> {
                    if (field.getType() == FieldType.STRING) {
                        int maxLength = Integer.parseInt(constraint.value());
                        if (value.toString().length() > maxLength) {
                            return "Field '" + field.getName() + "' must not exceed " + maxLength + " characters";
                        }
                    }
                }
                case MIN -> {
                    if (field.getType() == FieldType.INTEGER || field.getType() == FieldType.FLOAT) {
                        double minValue = Double.parseDouble(constraint.value());
                        double fieldValue = ((Number) value).doubleValue();
                        if (fieldValue < minValue) {
                            return "Field '" + field.getName() + "' must be at least " + minValue;
                        }
                    }
                }
                case MAX -> {
                    if (field.getType() == FieldType.INTEGER || field.getType() == FieldType.FLOAT) {
                        double maxValue = Double.parseDouble(constraint.value());
                        double fieldValue = ((Number) value).doubleValue();
                        if (fieldValue > maxValue) {
                            return "Field '" + field.getName() + "' must not exceed " + maxValue;
                        }
                    }
                }
                case REGEX -> {
                    if (field.getType() == FieldType.STRING) {
                        String pattern = constraint.value();
                        if (!value.toString().matches(pattern)) {
                            return "Field '" + field.getName() + "' does not match required pattern";
                        }
                    }
                }
                // NOT_NULL, UNIQUE, DEFAULT, FOREIGN_KEY are handled by the database
                // PRIMARY_KEY is not applicable for user fields
            }
        } catch (Exception e) {
            log.warn("Error validating constraint {} for field {}: {}",
                    constraint.constraintType(), field.getName(), e.getMessage());
            return "Invalid constraint validation for field '" + field.getName() + "'";
        }
        return null;
    }

    public boolean stopApi(String userApiKey, String entityName) {
        String key = registry.getKeyForProject(userApiKey, entityName);
        return registry.stop(key);
    }

    public Optional<Integer> getPort(String userApiKey, String entityName) {
        String key = registry.getKeyForProject(userApiKey, entityName);
        return registry.getPort(key);
    }

    public List<String> listActiveApis() {
        return registry.listKeys();
    }
}