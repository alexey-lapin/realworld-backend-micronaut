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

import com.github.al.realworld.api.query.GetProfile;
import com.github.al.realworld.api.query.GetProfileResult;
import com.github.al.realworld.application.ProfileAssembler;
import com.github.al.bus.QueryHandler;
import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.model.User;
import com.github.al.realworld.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import javax.transaction.Transactional;

import static com.github.al.realworld.application.exception.ResourceNotFoundException.notFound;

@RequiredArgsConstructor
@Singleton
public class GetProfileHandler implements QueryHandler<GetProfileResult, GetProfile> {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public GetProfileResult handle(GetProfile query) {
        Profile currentProfile = userRepository.findByUsername(query.getCurrentUsername())
                .map(User::getProfile)
                .orElse(null);

        Profile profile = userRepository.findByUsername(query.getUsername())
                .map(User::getProfile)
                .orElseThrow(() -> notFound("user [name=%s] does not exist", query.getUsername()));

        return new GetProfileResult(ProfileAssembler.assemble(profile, currentProfile));
    }
}
