package at.kalwoda.nocodeapi.service;

import at.kalwoda.nocodeapi.domain.*;
import at.kalwoda.nocodeapi.foundation.Base58;
import at.kalwoda.nocodeapi.persistance.UserRepository;
import at.kalwoda.nocodeapi.service.commands.UserCommands;
import at.kalwoda.nocodeapi.service.commands.UserCommands.LoginCommand;
import at.kalwoda.nocodeapi.service.commands.UserCommands.RegisterCommand;
import at.kalwoda.nocodeapi.service.dtos.user.UserMinimalDto;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        return checkUser(username);
    }

    public Optional<UserMinimalDto> getMinimal(String username) {
        checkUser(username);
        return userRepository.findByUsernameMinimal(new Username(username));
    }

    public User checkUser(String username) {
        return userRepository.findByUsername(new Username(username))
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}
