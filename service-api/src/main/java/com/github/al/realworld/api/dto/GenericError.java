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

import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The error response envelope, mapping each field at fault to its messages.
 */
@Serdeable
public record GenericError(
        Map<String, List<String>> errors
) {

    public static GenericError of(String field, String message) {
        return new GenericError(Map.of(field, List.of(message)));
    }

    /**
     * Groups {@code field} to {@code message} pairs, preserving encounter order and merging
     * repeated fields into a single list.
     */
    public static GenericError ofPairs(List<Map.Entry<String, String>> pairs) {
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (var pair : pairs) {
            grouped.computeIfAbsent(pair.getKey(), _ -> new ArrayList<>()).add(pair.getValue());
        }
        return new GenericError(grouped);
    }

}
