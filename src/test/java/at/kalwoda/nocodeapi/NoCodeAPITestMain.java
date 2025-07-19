package at.kalwoda.nocodeapi;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class NoCodeAPITestMain {

    private @Value("${test.docker.db.username}") String username;
    private @Value("${test.docker.db.password}") String password;
    private @Value("${test.docker.db.port}") Integer port;
    private @Value("${test.docker.db.name}") String dbName;
    private @Value("${test.docker.db.image.name}") String fullImageName;
    private @Value("${test.docker.db.container.name}") String containerName;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        final Integer containerPort = 5432;

        PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(containerPort));
        return new PostgreSQLContainer<>(DockerImageName.parse(fullImageName))
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(containerName);
                    cmd.withHostConfig(new HostConfig().withPortBindings(portBinding));
                })
                .withUsername(username)
                .withPassword(password)
                .withDatabaseName(dbName)
                .withReuse(true);

    }

    public static void main(String[] args) {
        SpringApplication.from(NoCodeAPITestMain::main)
                .with(NoCodeAPITestMain.class)
                .run(args);
    }

}
