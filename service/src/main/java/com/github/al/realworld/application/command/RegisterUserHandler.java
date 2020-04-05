package com.github.al.realworld.application.command;

import com.github.al.bus.CommandHandler;
import com.github.al.realworld.api.command.RegisterUser;
import com.github.al.realworld.api.command.RegisterUserResult;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.security.authentication.providers.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.Optional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class RegisterUserHandler implements CommandHandler<RegisterUserResult, RegisterUser> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public RegisterUserResult handle(RegisterUser command) {
        Optional<User> userByEmailOptional = userRepository.findByEmail(command.getEmail());
        if (userByEmailOptional.isPresent()) {
            throw invalidRequest("user [email=%s] already exists", command.getEmail());
        }
        Optional<User> userByUsernameOptional = userRepository.findByUsername(command.getUsername());
        if (userByUsernameOptional.isPresent()) {
            throw invalidRequest("user [name=%s] already exists", command.getUsername());
        }

        Profile profile = Profile.builder()
                .username(command.getUsername())
                .build();

        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .profile(profile)
                .build();
        userRepository.save(user);

        return new RegisterUserResult(UserAssembler.assemble(user, jwtService));
    }
}
