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
package com.github.al.realworld.infrastructure.db.jpa;

import com.github.al.realworld.domain.model.Profile;
import com.github.al.realworld.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.Optional;

@RequiredArgsConstructor
@Singleton
public class JpaProfileRepositoryAdapter implements ProfileRepository {

    private final DataProfileRepository repository;

    @Override
    public Optional<Profile> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public Profile save(Profile profile) {
        if (repository.existsById(profile.getUsername())) {
            return repository.update(profile);
        }
        return repository.save(profile);
    }
}
