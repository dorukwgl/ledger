package com.doruk.application.security;

public interface PasswordEncoder {
    String encode(String password);
    Boolean matches(String password, String encodedPassword);
}
