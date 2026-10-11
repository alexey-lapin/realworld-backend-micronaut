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

import com.github.al.realworld.api.dto.JsonNullable;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Deserializer;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.Serde;
import io.micronaut.serde.Serializer;
import io.micronaut.serde.exceptions.SerdeException;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 * Reads a {@link JsonNullable} so that an absent property stays {@code null} while a JSON null
 * becomes an instance holding {@code null}, and writes it as its contained value.
 */
@Singleton
public class JsonNullableSerde implements Serde<JsonNullable<?>> {

    @Override
    public Deserializer<JsonNullable<?>> createSpecific(DecoderContext context,
                                                        Argument<? super JsonNullable<?>> type)
            throws SerdeException {
        var valueType = valueType(type);
        Deserializer<? extends Object> valueDeserializer = context.findDeserializer(valueType)
                .createSpecific(context, valueType);
        return new Deserializer<>() {

            @Override
            public JsonNullable<?> deserialize(Decoder decoder,
                                               DecoderContext context,
                                               Argument<? super JsonNullable<?>> type) throws IOException {
                return new JsonNullable<>(valueDeserializer.deserializeNullable(decoder, context, valueType));
            }

            @Override
            public JsonNullable<?> deserializeNullable(Decoder decoder,
                                                       DecoderContext context,
                                                       Argument<? super JsonNullable<?>> type) throws IOException {
                return decoder.decodeNull() ? new JsonNullable<>(null) : deserialize(decoder, context, type);
            }

            @Override
            public @Nullable JsonNullable<?> getDefaultValue(DecoderContext context,
                                                             Argument<? super JsonNullable<?>> type) {
                // the property was absent
                return null;
            }

        };
    }

    @Override
    public JsonNullable<?> deserialize(Decoder decoder,
                                       DecoderContext context,
                                       Argument<? super JsonNullable<?>> type) throws IOException {
        return createSpecific(context, type).deserialize(decoder, context, type);
    }

    @Override
    public JsonNullable<?> deserializeNullable(Decoder decoder,
                                               DecoderContext context,
                                               Argument<? super JsonNullable<?>> type) throws IOException {
        return createSpecific(context, type).deserializeNullable(decoder, context, type);
    }

    @Override
    public @Nullable JsonNullable<?> getDefaultValue(DecoderContext context, Argument<? super JsonNullable<?>> type) {
        return null;
    }

    @Override
    public void serialize(Encoder encoder,
                          EncoderContext context,
                          Argument<? extends JsonNullable<?>> type,
                          JsonNullable<?> value) throws IOException {
        if (value.isNull()) {
            encoder.encodeNull();
            return;
        }
        var valueType = valueType(type);
        Serializer<Object> serializer = context.findSerializer(valueType).createSpecific(context, valueType);
        serializer.serialize(encoder, context, valueType, value.value());
    }

    @SuppressWarnings("unchecked")
    private static Argument<Object> valueType(Argument<?> type) {
        var typeParameters = type.getTypeParameters();
        return typeParameters.length == 0 ? Argument.OBJECT_ARGUMENT : (Argument<Object>) typeParameters[0];
    }

}
