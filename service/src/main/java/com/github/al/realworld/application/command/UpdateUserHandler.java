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

import com.github.al.bus.CommandHandler;
import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.command.UpdateUserResult;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.realworld.application.service.PasswordEncoder;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import jakarta.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.Exceptions.badRequest;
import static com.github.al.realworld.application.exception.Exceptions.notFound;

@RequiredArgsConstructor
@Singleton
public class UpdateUserHandler implements CommandHandler<UpdateUserResult, UpdateUser> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    @Transactional
    @Override
    public UpdateUserResult handle(UpdateUser command) {
        User user = userRepository.findByUsername(command.getCurrentUsername())
                .orElseThrow(() -> notFound("user [name=%s] does not exist", command.getCurrentUsername()));

        if (command.getUsername() != null
                && !command.getUsername().equals(user.getUsername())
                && userRepository.findByUsername(command.getUsername()).isPresent()) {
            throw badRequest("user [name=%s] already exists", command.getUsername());
        }

        if (command.getEmail() != null
                && !command.getEmail().equals(user.getEmail())
                && userRepository.findByEmail(command.getEmail()).isPresent()) {
            throw badRequest("user [email=%s] already exists", command.getEmail());
        }

        User alteredUser = user.toBuilder()
                .email(command.getEmail() != null ? command.getEmail() : user.getEmail())
                .username(command.getUsername() != null ? command.getUsername() : user.getUsername())
                .password(command.getPassword() != null ? encoder.encode(command.getPassword()) : user.getPassword())
                .bio(command.getBio() != null ? command.getBio() : user.getBio())
                .image(command.getImage() != null ? command.getImage() : user.getImage())
                .build();

        User savedUser = userRepository.save(alteredUser);

        return new UpdateUserResult(UserAssembler.assemble(savedUser, jwtService));
    }

}
