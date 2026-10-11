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
package com.github.al.realworld.bus;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MicronautTest(startApplication = false)
class DefaultBusTest {

    @Inject
    private Bus bus;

    @Test
    void should_dispatchCommandToItsHandler() {
        assertThat(bus.executeCommand(new Greet("bus"))).isEqualTo("hello bus");
    }

    @Test
    void should_dispatchQueryToItsHandler() {
        assertThat(bus.executeQuery(new Length("four"))).isEqualTo(4);
    }

    @Test
    void should_resolveHandler_when_typeArgumentsComeFromSuperclass() {
        assertThat(bus.executeCommand(new Shout("bus"))).isEqualTo("BUS");
    }

    @Test
    void should_failClearly_when_noHandlerIsRegistered() {
        assertThatThrownBy(() -> bus.executeCommand(new Unhandled()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(Unhandled.class.getName());
    }

    record Greet(String name) implements Command<String> {
    }

    @Singleton
    static class GreetHandler implements CommandHandler<String, Greet> {

        @Override
        public String handle(Greet command) {
            return "hello " + command.name();
        }

    }

    record Length(String text) implements Query<Integer> {
    }

    @Singleton
    static class LengthHandler implements QueryHandler<Integer, Length> {

        @Override
        public Integer handle(Length query) {
            return query.text().length();
        }

    }

    record Shout(String text) implements Command<String> {
    }

    abstract static class TextCommandHandler<C extends Command<String>> implements CommandHandler<String, C> {
    }

    @Singleton
    static class ShoutHandler extends TextCommandHandler<Shout> {

        @Override
        public String handle(Shout command) {
            return command.text().toUpperCase();
        }

    }

    record Unhandled() implements Command<String> {
    }

}
