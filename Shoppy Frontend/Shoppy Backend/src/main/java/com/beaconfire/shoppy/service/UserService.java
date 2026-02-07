package com.beaconfire.shoppy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.beaconfire.shoppy.exception.InvalidCredentialsException;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.repository.UserRepository;
import com.beaconfire.shoppy.util.JwtUtil;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional("hibernateTransactionManager")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists.");
        }
        user.setRole(1);
        userRepository.saveUser(user);
    }

//    public String loginUser(String username, String password) {
//        User user = userRepository.findByUsername(username);
//
//        if (user == null || !user.getPassword().equals(password)) {
//            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
//        }
//
//        // user role 1 = customer, 0 = seller
//        if (user.getRole() == 1) {
//            return "Customer Home Page"; // Placeholder for the customer home page logic
//        } else if (user.getRole() == 0) {
//            return "Seller Dashboard"; // Placeholder for the seller dashboard logic
//        } else {
//            throw new IllegalStateException("User role is undefined.");
//        }
//    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        // Generate JWT token
        //return jwtUtil.generateToken(username);
        return jwtUtil.generateToken(username, user.getRole() == 0 ? "ROLE_ADMIN" : "ROLE_USER");
    }


    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            userRepository.deleteUser(user);
        }
    }
}