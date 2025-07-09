package at.kalwoda.nocodeapi.service.db;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.persistance.FieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class DdlGenerator {

    private final FieldRepository fieldRepository;

    public String generateCreateTableSql(EntityModel entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(entity.getName().toLowerCase()).append(" (");

        for (Field field : entity.getFields()) {
            if (isManyToMany(field)) continue;

            sb.append(field.getName().toLowerCase()).append(" ");
            sb.append(switch (field.getType()) {
                case STRING -> "VARCHAR(255)";
                case INTEGER -> "INTEGER";
                case BOOLEAN -> "BOOLEAN";
                case FLOAT -> "DOUBLE PRECISION";
                case DATE -> "DATE";
            });

            for (ConstraintDefinition constraint : field.getConstraints()) {
                switch (constraint.constraintType()) {
                    case DEFAULT -> {
                        if (constraint.value() != null && !constraint.value().isEmpty()) {
                            sb.append(" DEFAULT '").append(constraint.value()).append("'");
                        }
                    }
                    case NOT_NULL -> sb.append(" NOT NULL");
                    case UNIQUE -> sb.append(" UNIQUE");
                    case MIN_LENGTH -> sb.append(" CHECK (LENGTH(")
                            .append(field.getName().toLowerCase()).append(") >= ")
                            .append(constraint.value()).append(")");
                    case MAX_LENGTH -> sb.append(" CHECK (LENGTH(")
                            .append(field.getName().toLowerCase()).append(") <= ")
                            .append(constraint.value()).append(")");
                    case REGEX -> sb.append(" CHECK (")
                            .append(field.getName().toLowerCase())
                            .append(" ~ '").append(constraint.value()).append("')");
                    case MIN -> sb.append(" CHECK (")
                            .append(field.getName().toLowerCase())
                            .append(" >= ").append(constraint.value()).append(")");
                    case MAX -> sb.append(" CHECK (")
                            .append(field.getName().toLowerCase())
                            .append(" <= ").append(constraint.value()).append(")");
                    case FOREIGN_KEY -> appendForeignKey(sb, field, constraint);
                    case PRIMARY_KEY -> sb.append(" PRIMARY KEY");
                }
            }

            sb.append(", ");
        }

        sb.setLength(sb.length() - 2);
        sb.append(");");
        return sb.toString();
    }

    private void appendForeignKey(StringBuilder sb, Field field, ConstraintDefinition constraint) {
        ForeignKeyMetadata fk = constraint.foreignKey();
        if (fk == null) return;
        sb.append(" REFERENCES ")
                .append(fk.targetEntity().toLowerCase())
                .append("(").append(fk.targetField().toLowerCase()).append(")")
                .append(" ON DELETE CASCADE");
        if (fk.relationType() == RelationshipType.ONE_TO_ONE) {
            sb.append(" UNIQUE");
        }
    }

    private boolean isManyToMany(Field field) {
        return field.getConstraints().stream()
                .filter(c -> c.constraintType() == Constraints.FOREIGN_KEY)
                .map(ConstraintDefinition::foreignKey)
                .anyMatch(fk -> fk != null && fk.relationType() == RelationshipType.MANY_TO_MANY);
    }

    public String generateJunctionTableSql(Field field) {
        return field.getConstraints().stream()
                .filter(c -> c.constraintType() == Constraints.FOREIGN_KEY)
                .map(ConstraintDefinition::foreignKey)
                .filter(fk -> fk != null && fk.relationType() == RelationshipType.MANY_TO_MANY)
                .findFirst()
                .map(fk -> {
                    String source = field.getEntity().getName().toLowerCase();
                    String target = fk.targetEntity().toLowerCase();
                    String junction = source + "_" + target;
                    return "CREATE TABLE IF NOT EXISTS " + junction + " ("
                            + source + "_id INTEGER NOT NULL, "
                            + target + "_id INTEGER NOT NULL, "
                            + "PRIMARY KEY (" + source + "_id, " + target + "_id), "
                            + "FOREIGN KEY (" + source + "_id) REFERENCES " + source + "(id) ON DELETE CASCADE, "
                            + "FOREIGN KEY (" + target + "_id) REFERENCES " + target + "(id) ON DELETE CASCADE);";
                })
                .orElse(null);
    }

    public void executeSchema(DataSource ds, List<EntityModel> entities) {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            for (EntityModel entity : entities) {
                String sql = generateCreateTableSql(entity);
                log.debug("Executing SQL: {}", sql);
                stmt.execute(sql);
            }

            Set<String> created = new HashSet<>();
            for (EntityModel entity : entities) {
                for (Field field : entity.getFields()) {
                    String junction = generateJunctionTableSql(field);
                    if (junction != null) {
                        String name = extractJunctionTableName(junction);
                        if (!created.contains(name)) {
                            log.debug("Executing junction table SQL: {}", junction);
                            stmt.execute(junction);
                            created.add(name);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            log.error("Failed to execute DB schema: ", e);
            throw new RuntimeException("DB Schema failed", e);
        }
    }

    private String extractJunctionTableName(String sql) {
        int start = sql.indexOf("CREATE TABLE IF NOT EXISTS ") + 28;
        int end = sql.indexOf(" (", start);
        return sql.substring(start, end);
    }
}
