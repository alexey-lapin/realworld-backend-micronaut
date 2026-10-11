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
package com.github.al.realworld.api.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.Nullable;

/**
 * A payload field that distinguishes an absent JSON property from one explicitly set to null.
 *
 * <p>A {@code null} reference denotes an absent property. A non-null instance denotes a present
 * property, holding {@code null} when the client sent a JSON null.
 *
 * <p>Serialization unwraps to the contained value. Deserialization requires a deserializer that
 * maps an absent property to {@code null} and a JSON null to an instance holding {@code null}.
 *
 * @param <T>   the contained value type
 * @param value the contained value, {@code null} for a JSON null
 */
public record JsonNullable<T>(@Nullable T value) {

    /**
     * Returns the value contained in {@code field}, or {@code null} if the field is absent.
     */
    public static <T> @Nullable T unwrap(@Nullable JsonNullable<T> field) {
        return field == null ? null : field.value();
    }

    public boolean isNull() {
        return value == null;
    }

    @JsonValue
    public @Nullable T json() {
        return value;
    }

}
