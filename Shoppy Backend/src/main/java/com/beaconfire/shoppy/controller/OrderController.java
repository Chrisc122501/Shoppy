package com.beaconfire.shoppy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.beaconfire.shoppy.dto.OrderItemDTO;
import com.beaconfire.shoppy.exception.NotEnoughInventoryException;
import com.beaconfire.shoppy.model.CustomerOrder;
import com.beaconfire.shoppy.model.OrderItem;
import com.beaconfire.shoppy.service.OrderService;
import com.beaconfire.shoppy.util.JwtUtil;

//import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, List<OrderItemDTO>> orderRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            CustomerOrder order = orderService.placeOrder(username, orderRequest.get("order"));
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (NotEnoughInventoryException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    @GetMapping("/all")
//    public ResponseEntity<?> getAllOrders() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        List<CustomerOrder> orders = orderService.getAllOrders(username);
//        return ResponseEntity.ok(orders);
//    }

//    @GetMapping("/all")
//    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String token) {
//        String username = jwtUtil.extractUsername(token.substring(7));
//        String role = jwtUtil.extractRole(token);
//
//        try {
//            if ("ROLE_ADMIN".equals(role)) {
//                // Admin can see all orders
//                List<CustomerOrder> allOrders = orderService.getAllOrdersForAdmin();
//                return ResponseEntity.ok(allOrders);
//            } else {
//                // Users can see only their orders
//                List<CustomerOrder> userOrders = orderService.getAllOrders(username);
//                return ResponseEntity.ok(userOrders);
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        }
//    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            System.out.println("Username: " + username + ", Role: " + role);

            if ("ROLE_ADMIN".equals(role)) {
                List<CustomerOrder> allOrders = orderService.getAllOrdersForAdmin();
                return ResponseEntity.ok(allOrders);
            } else {
                List<CustomerOrder> userOrders = orderService.getAllOrders(username);
                return ResponseEntity.ok(userOrders);
            }
        } catch (Exception e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied or invalid token.");
        }
    }



//    @GetMapping("/{id}")
//    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, @RequestHeader("Authorization") String token) {
//        String username = jwtUtil.extractUsername(token.substring(7));
//        String role = jwtUtil.extractRole(token);
//
//        try {
//            if ("ROLE_ADMIN".equals(role)) {
//                // Admin can view any order
//                CustomerOrder order = orderService.getOrderByIdForAdmin(id);
//                return ResponseEntity.ok(order);
//            } else {
//                // Users can only view their own orders
//                CustomerOrder order = orderService.getOrderByIdForUser(id, username);
//                return ResponseEntity.ok(order);
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        }
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Received Authorization header when trying to get order details: " + token);

            // Extract the actual token
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            System.out.println("Token after removing 'Bearer': " + token);

            // Parse the token
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            System.out.println("Extracted username: " + username);
            System.out.println("Extracted role: " + role);

            if ("ROLE_ADMIN".equals(role)) {
                // Admin can view any order
                CustomerOrder order = orderService.getOrderByIdForAdmin(id);
                return ResponseEntity.ok(order);
            } else {
                // Users can only view their own orders
                CustomerOrder order = orderService.getOrderByIdForUser(id, username);
                return ResponseEntity.ok(order);
            }
        } catch (Exception e) {
            System.err.println("Error processing token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


//    @PatchMapping("/{id}/cancel")
//    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            CustomerOrder updatedOrder = orderService.cancelOrder(id, username);
//            return ResponseEntity.ok("Order status updated to Canceled.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        }
//    }

//    @PatchMapping("/{id}/cancel")
//    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
//        String username = jwtUtil.extractUsername(token.substring(7));
//        String role = jwtUtil.extractRole(token);
//
//        try {
//            if ("ROLE_ADMIN".equals(role)) {
//                // Admin can cancel any order
//                CustomerOrder updatedOrder = orderService.cancelOrderForAdmin(id);
//                return ResponseEntity.ok("Order with ID " + id + " status updated to Canceled.");
//            } else {
//                // Users can only cancel their own orders
//                CustomerOrder updatedOrder = orderService.cancelOrder(id, username);
//                return ResponseEntity.ok("Your order with ID " + id + " status updated to Canceled.");
//            }
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        }
//    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        String role = jwtUtil.extractRole(token);

        try {
            if ("ROLE_ADMIN".equals(role)) {
                // Admin can cancel any order
                CustomerOrder updatedOrder = orderService.cancelOrderForAdmin(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Order with ID " + id + " status updated to Canceled.");
                return ResponseEntity.ok(response); // Return JSON response
            } else {
                // Users can only cancel their own orders
                CustomerOrder updatedOrder = orderService.cancelOrder(id, username);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Your order with ID " + id + " status updated to Canceled.");
                return ResponseEntity.ok(response); // Return JSON response
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }


    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        String role = jwtUtil.extractRole(token);

        try {
            if (!"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can complete orders.");
            }

            CustomerOrder updatedOrder = orderService.completeOrderForAdmin(id);

            // Return the updated order details as JSON
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order with ID " + id + " marked as Completed.");
            response.put("order", updatedOrder);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}