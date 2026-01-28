package com.timeleafing.qqbot.domain.auth;

import com.timeleafing.qqbot.exception.TokenAuthenticationException;
import org.springframework.security.core.Authentication;

public interface AuthenticationService {

    Authentication buildAuthenticationFor(String token) throws TokenAuthenticationException;

}
