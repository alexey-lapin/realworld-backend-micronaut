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
package com.github.al.realworld.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import io.micronaut.security.token.jwt.signature.rsa.RSASignatureGeneratorConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Signs and verifies tokens with an RSA key pair generated at startup.
 *
 * <p>Nothing secret ships with the application, at the cost that a restart invalidates every
 * issued token and separate instances cannot verify each other's tokens; both are acceptable for
 * this single-instance demo. The {@code generator} name makes Micronaut use it for signing, and
 * as an RSA signature configuration it also verifies.
 */
@Named("generator")
@Singleton
public class GeneratedRsaSignatureConfiguration implements RSASignatureGeneratorConfiguration {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;

    public GeneratedRsaSignatureConfiguration() throws NoSuchAlgorithmException {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var keyPair = generator.generateKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public JWSAlgorithm getJwsAlgorithm() {
        return JWSAlgorithm.RS256;
    }

}
