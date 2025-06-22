package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

public class UserCommands {
    public record RegisterCommand(
            @NotBlank(message = "Username is required")
            String username,

            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {
    }

    public record LoginCommand(
            @NotBlank(message = "Username is required")
            String username,

            @NotBlank(message = "Password is required")
            String password
    ) {
    }

}
