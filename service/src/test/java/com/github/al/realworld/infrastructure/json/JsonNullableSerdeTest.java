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
package com.github.al.realworld.infrastructure.json;

import com.github.al.realworld.api.command.CreateArticle;
import com.github.al.realworld.api.command.UpdateArticle;
import com.github.al.realworld.api.command.UpdateUser;
import com.github.al.realworld.api.dto.JsonNullable;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest(startApplication = false)
class JsonNullableSerdeTest {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Validator validator;

    @Test
    void should_keepAbsentAndNullApart_when_deserializing() throws IOException {
        var data = readUser("""
                {"user":{"bio":null,"image":"img"}}""");

        assertThat(data.email()).isNull();
        assertThat(data.bio()).isNotNull();
        assertThat(data.bio().isNull()).isTrue();
        assertThat(data.image()).isEqualTo(new JsonNullable<>("img"));
    }

    @Test
    void should_readListValue_when_deserializingTagList() throws IOException {
        var data = objectMapper.readValue("""
                {"article":{"tagList":["a","b"]}}""", UpdateArticle.class).article();

        assertThat(data.title()).isNull();
        assertThat(data.tagList()).isEqualTo(new JsonNullable<>(List.of("a", "b")));
    }

    @Test
    void should_omitAbsentAndWriteNull_when_serializing() throws IOException {
        var data = new UpdateUser.Data(null, new JsonNullable<>("name"), null, new JsonNullable<>(null), null);

        var json = objectMapper.writeValueAsString(new UpdateUser(data));

        assertThat(json).isEqualTo("""
                {"user":{"username":"name","image":null}}""");
    }

    @Test
    void should_applyConstraintsToPresentValuesOnly() throws IOException {
        var data = readUser("""
                {"user":{"bio":null}}""");

        assertThat(validator.validate(new UpdateUser(data))).isEmpty();
    }

    @Test
    void should_rejectExplicitNull_when_fieldIsRequired() throws IOException {
        var data = readUser("""
                {"user":{"email":null,"username":""}}""");

        var violations = validator.validate(new UpdateUser(data));

        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
                .containsExactlyInAnyOrder("user.email", "user.username");
    }

    @Test
    void should_rejectNullTagNames() {
        var update = new UpdateArticle("slug", new UpdateArticle.Data(
                null, null, null, new JsonNullable<>(Arrays.asList(null, "x"))));
        var create = new CreateArticle(new CreateArticle.Data(
                "title", "description", "body", Arrays.asList("", "x")));

        assertThat(validator.validate(update)).hasSize(1);
        assertThat(validator.validate(create)).hasSize(1);
    }

    private UpdateUser.Data readUser(String json) throws IOException {
        return objectMapper.readValue(json, UpdateUser.class).user();
    }

}
