package com.beaconfire.shoppy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "your_secret_key";

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

//    public String extractRole(String token) {
//        try {
//            System.out.println("Raw token: " + token);
//            if (token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            }
//            System.out.println("Token for extracting role: " + token);
//
//            Claims claims = Jwts.parser()
//                    .setSigningKey(SECRET_KEY)
//                    .parseClaimsJws(token)
//                    .getBody();
//            System.out.println("Extracted role: " + claims.get("role", String.class));
//            return claims.get("role", String.class);
//        } catch (Exception e) {
//            System.err.println("Error extracting role: " + e.getMessage());
//            throw new MalformedJwtException("Invalid token: Unable to extract role.", e);
//        }
//    }

    public String extractRole(String token) {
        // Remove 'Bearer ' prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return parseToken(token).get("role", String.class);
    }

//    public String extractUsername(String token) {
//        try {
//            System.out.println("Token for extracting username: " + token);
//            Claims claims = Jwts.parser()
//                    .setSigningKey(SECRET_KEY)
//                    .parseClaimsJws(token)
//                    .getBody();
//            System.out.println("Extracted username: " + claims.getSubject());
//            return claims.getSubject();
//        } catch (Exception e) {
//            System.err.println("Error extracting username: " + e.getMessage());
//            throw new MalformedJwtException("Invalid token: Unable to extract username.", e);
//        }
//    }

    public String extractUsername(String token) {
        // Remove 'Bearer ' prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return parseToken(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username);
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    private Claims parseToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        }
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateToken(String token) {
        Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token); // Parses and validates the token
    }

}
