/*
 * MIT License
 *
 * Copyright (c) 2020 - present Alexey Lapin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.al.realworld.application.command;

import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.command.UpdateUserResult;
import com.github.al.realworld.api.dto.JsonNullable;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.AuthenticationService;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.application.service.PasswordEncoder;
import com.github.al.realworld.bus.CommandHandler;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;


import static com.github.al.realworld.application.exception.ConflictException.alreadyTaken;
import static com.github.al.realworld.application.exception.BadRequestException.badRequest;
import static com.github.al.realworld.application.exception.UnauthorizedException.invalidToken;
import static com.github.al.realworld.api.dto.JsonNullable.unwrap;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
@Singleton
public class UpdateUserHandler implements CommandHandler<UpdateUserResult, UpdateUser> {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public UpdateUserResult handle(UpdateUser command) {
        var currentUsername = authenticationService.getRequiredCurrentUserName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> invalidToken("user [name=%s] does not exist", currentUsername));

        var data = command.user();
        if (data.isEmpty()) {
            throw badRequest("body", "can't be empty", "update payload contains no fields");
        }

        var newUsername = unwrap(data.username());
        if (newUsername != null
            && !newUsername.equals(user.getUsername())
            && userRepository.findByUsername(newUsername).isPresent()) {
            throw alreadyTaken("username", "user [name=%s] already exists", newUsername);
        }

        var newEmail = unwrap(data.email());
        if (newEmail != null
            && !newEmail.equals(user.getEmail())
            && userRepository.findByEmail(newEmail).isPresent()) {
            throw alreadyTaken("email", "user [email=%s] already exists", newEmail);
        }

        var newPassword = unwrap(data.password());

        User alteredUser = user.toBuilder()
                .email(requireNonNullElse(newEmail, user.getEmail()))
                .username(requireNonNullElse(newUsername, user.getUsername()))
                .password(newPassword == null ? user.getPassword() : encoder.encode(newPassword))
                .bio(normalized(data.bio(), user.getBio()))
                .image(normalized(data.image(), user.getImage()))
                .build();

        User savedUser = userRepository.save(alteredUser);

        return new UpdateUserResult(UserAssembler.assemble(savedUser, jwtService));
    }

    /**
     * Returns the new value of a nullable field, treating an empty string as {@code null} and an
     * absent field as {@code current}.
     */
    private static @Nullable String normalized(@Nullable JsonNullable<String> field,
                                               @Nullable String current) {
        if (field == null) {
            return current;
        }
        var value = field.value();
        return value == null || value.isEmpty() ? null : value;
    }

}
