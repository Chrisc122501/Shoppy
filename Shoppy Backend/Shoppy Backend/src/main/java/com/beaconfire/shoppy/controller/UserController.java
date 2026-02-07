package com.beaconfire.shoppy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.beaconfire.shoppy.dto.LoginRequest;
import com.beaconfire.shoppy.exception.InvalidCredentialsException;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.service.UserService;
import com.beaconfire.shoppy.util.JwtUtil;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            //Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            //Generate token after successful authentication
            User user = userService.findUserByUsername(loginRequest.getUsername());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole() == 0 ? "ROLE_ADMIN" : "ROLE_USER");

            return ResponseEntity.ok("Bearer " + token);
        } catch (InvalidCredentialsException | BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password, HttpSession session) {
//        try {
//            String page = userService.loginUser(username, password);
//            session.setAttribute("loggedInUser", username); // store username in session
//            return ResponseEntity.ok("Login successful. Redirecting to: " + page);
//        } catch (InvalidCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }
}
