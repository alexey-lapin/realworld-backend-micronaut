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
package com.github.al.realworld.application.query;

import com.github.al.realworld.api.query.GetCurrentUser;
import com.github.al.realworld.api.query.GetCurrentUserResult;
import com.github.al.realworld.application.UserAssembler;
import com.github.al.realworld.application.service.JwtService;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.InvalidRequestException.invalidRequest;

@RequiredArgsConstructor
@Singleton
public class GetCurrentUserHandler implements QueryHandler<GetCurrentUserResult, GetCurrentUser> {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public GetCurrentUserResult handle(GetCurrentUser query) {
        User user = userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> invalidRequest("user [name=%s] does not exist", query.getUsername()));

        return new GetCurrentUserResult(UserAssembler.assemble(user, jwtService));
    }
}
