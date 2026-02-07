package com.beaconfire.shoppy.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.beaconfire.shoppy.security.CustomAuthenticationProvider;
import com.beaconfire.shoppy.util.JwtUtil;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtUtil jwtUtil, CustomAuthenticationProvider authenticationProvider) {
        this.jwtUtil = jwtUtil;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/signup", "/login").permitAll()
                .antMatchers("/products/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers(HttpMethod.PATCH, "/products/**").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.PATCH, "/orders/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers(HttpMethod.PATCH, "/orders/**/complete").hasAuthority("ROLE_ADMIN")
                .antMatchers("/orders/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                .antMatchers(HttpMethod.GET, "/products/profit/**").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.GET, "/products/popular/**").hasAuthority("ROLE_ADMIN")
                .antMatchers(HttpMethod.POST, "/products").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()  // authentication needed for all other endpoints
                .and()
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
