package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.ApiKey;
import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.domain.User;
import at.kalwoda.nocodeapi.domain.Username;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.ProjectRepository;
import at.kalwoda.nocodeapi.persistance.UserRepository;
import at.kalwoda.nocodeapi.service.commands.ProjectCommands;
import at.kalwoda.nocodeapi.service.commands.ProjectCommands.CreateProjectCommand;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<Project> getAllProjects(String username) {
        return userRepository.findByUsername(new Username(username)).map(User::getProjects).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
    }

    public Project getProject(String username, String apiKey) {
        User user = userService.checkUser(username);

        return projectRepository.findByUserAndApiKey(user, new ApiKey(apiKey))
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    public Project createProject(String username, @Valid CreateProjectCommand command) {
        User user = userService.checkUser(username);

        ApiKey apiKey;
        do {
            apiKey = new ApiKey("p_" + Base58.random(16));
        } while (projectRepository.findByApiKey(apiKey).isPresent());

        Project project = Project.builder()
                .apiKey(apiKey)
                .name(command.name())
                .description(command.description())
                .createdAt(new Date())
                .user(user)
                .promptText("Test")
                .build();

        return projectRepository.save(project);
    }
}
