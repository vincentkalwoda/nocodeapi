package at.kalwoda.nocodeapi.service.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import javax.sql.DataSource;

@Slf4j
@Service
public class DatabaseProvisioningService {

    private final String dbAdminUrl = "jdbc:postgresql://localhost:5432/postgres";
    private final String dbAdminUser = "nocodeapiowner";
    private final String dbAdminPassword = "nocodeapiowner";

    public void createDatabaseForProject(String dbName) {
        try (Connection conn = DriverManager.getConnection(dbAdminUrl, dbAdminUser, dbAdminPassword);
             Statement stmt = conn.createStatement()) {

            String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
            var rs = stmt.executeQuery(checkSql);
            if (!rs.next()) {
                stmt.execute("CREATE DATABASE \"" + dbName + "\";");
                log.info("Created DB: {}", dbName);
            } else {
                log.info("Database '{}' already exists, skipping creation.", dbName);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DB: " + dbName, e);
        }
    }


    public static DataSource createDataSource(String dbName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/" + dbName);
        ds.setUsername("nocodeapiowner");
        ds.setPassword("nocodeapiowner");
        return ds;
    }
}

