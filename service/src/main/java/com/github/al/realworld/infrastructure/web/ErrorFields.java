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
package com.github.al.realworld.infrastructure.web;

/**
 * Derives error envelope keys from paths reported by validation and binding errors.
 */
final class ErrorFields {

    static final String FALLBACK = "body";

    private ErrorFields() {
    }

    /**
     * Returns the last segment of a {@code .} or {@code /} separated path, without any index or
     * container element suffix, falling back to {@code body}.
     */
    static String leaf(String path) {
        var leaf = path.substring(Math.max(path.lastIndexOf('.'), path.lastIndexOf('/')) + 1);
        var end = leaf.length();
        for (char c : new char[]{'[', '<'}) {
            var index = leaf.indexOf(c);
            if (index >= 0) {
                end = Math.min(end, index);
            }
        }
        leaf = leaf.substring(0, end);
        return leaf.isEmpty() ? FALLBACK : leaf;
    }

}
