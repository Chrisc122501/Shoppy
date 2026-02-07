package com.beaconfire.shoppy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.beaconfire.shoppy.exception.InvalidCredentialsException;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.service.UserService;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.findUserByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        // Assign roles based on user role
        String role = user.getRole() == 0 ? "ROLE_ADMIN" : "ROLE_USER";

        return new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
