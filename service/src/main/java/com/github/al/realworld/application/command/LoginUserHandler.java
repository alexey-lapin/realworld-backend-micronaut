package com.github.al.realworld.application.command;

import com.github.al.bus.CommandHandler;
import com.github.al.realworld.api.command.LoginUser;
import com.github.al.realworld.api.command.LoginUserResult;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import io.micronaut.security.authentication.providers.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class LoginUserHandler implements CommandHandler<LoginUserResult, LoginUser> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public LoginUserResult handle(LoginUser command) {
        User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> invalidRequest("user [email=%s] does not exist", command.getEmail()));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw invalidRequest("user [name=%s] password is incorrect");
        }

        return new LoginUserResult(UserAssembler.assemble(user, jwtService));
    }
}
