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

    private static final String DB_HOST = "nocodeapidb";
    private static final String DB_PORT = "5432";
    private static final String DB_USER = "nocodeapiowner";
    private static final String DB_PASS = "nocodeapiowner";


    public void createDatabaseForProject(String dbName) {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + dbName, DB_USER, DB_PASS);
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
        ds.setJdbcUrl("jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + dbName);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASS);
        return ds;
    }
}

