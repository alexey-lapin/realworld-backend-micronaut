package com.github.al.realworld.application.service;

public interface PasswordEncoder {

    /**
     * @param rawPassword The plain text password
     * @return The result of encoding the password
     */
    String encode(String rawPassword);

    /**
     *
     * @param rawPassword The plain text password
     * @param encodedPassword The encoded password to match against
     * @return true if the passwords match
     */
    boolean matches(String rawPassword, String encodedPassword);

}
