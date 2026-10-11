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
package com.github.al.realworld.application.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugServiceTest {

    private static final String RANDOM_SUFFIX_PATTERN = "[a-z0-9]{12}";

    private final SlugService slugService = new SlugService();

    @Test
    void should_appendRandomSuffix_when_inputHasNoSeparators() {
        assertSlugMatches("qwer", slugService.makeSlug("qwer"));
    }

    @Test
    void should_trimTrailingHyphen_when_inputEndsWithHyphen() {
        assertSlugMatches("qwer", slugService.makeSlug("qwer-"));
    }

    @Test
    void should_replaceWhitespaceWithHyphen_when_inputHasSpaces() {
        assertSlugMatches("qw-er", slugService.makeSlug("qw er-"));
    }

    @Test
    void should_lowercaseOutput_when_inputHasUppercase() {
        assertSlugMatches("qw-er", slugService.makeSlug("Qw Er-"));
    }

    @Test
    void should_collapseRepeatedHyphens_when_inputHasMultipleHyphens() {
        assertSlugMatches("qw-er", slugService.makeSlug("qw--er---"));
    }

    @Test
    void should_replacePunctuationWithHyphen_when_inputHasPunctuation() {
        assertSlugMatches("qw-er", slugService.makeSlug("qw.er,"));
    }

    @Test
    void should_dropAccents_when_inputHasAccentedLetters() {
        assertSlugMatches("cafe", slugService.makeSlug("Café"));
    }

    @Test
    void should_returnRandomSuffixOnly_when_inputHasNoSlugChars() {
        assertThat(slugService.makeSlug("!!!")).matches(RANDOM_SUFFIX_PATTERN);
        assertThat(slugService.makeSlug("日本語")).matches(RANDOM_SUFFIX_PATTERN);
    }

    @Test
    void should_produceDistinctSlugs_when_titlesAreEqual() {
        assertThat(slugService.makeSlug("same")).isNotEqualTo(slugService.makeSlug("same"));
    }

    private static void assertSlugMatches(String expectedBase, String slug) {
        assertThat(slug).matches(expectedBase + "-" + RANDOM_SUFFIX_PATTERN);
    }

}
