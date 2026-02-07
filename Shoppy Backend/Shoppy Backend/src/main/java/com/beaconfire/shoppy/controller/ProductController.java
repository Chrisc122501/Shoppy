package com.beaconfire.shoppy.controller;

import com.beaconfire.shoppy.dto.CreateProductRequest;
import com.beaconfire.shoppy.model.OrderItem;
import com.beaconfire.shoppy.model.Product;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.service.OrderService;
import com.beaconfire.shoppy.service.ProductService;
import com.beaconfire.shoppy.util.JwtUtil;
import com.beaconfire.shoppy.util.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.stream.Collectors;

//import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

//    @GetMapping("/all")
//    @JsonView(Views.User.class)
//    public ResponseEntity<?> getInStockProducts() {
//        List<Product> products = productService.getAllInStock();
//        return ResponseEntity.ok(products);
//    }

//    @GetMapping("/all")
//    public ResponseEntity<?> getInStockProducts(@RequestHeader("Authorization") String token) throws JsonProcessingException {
//        String role = jwtUtil.extractRole(token.substring(7));
//
//        List<Product> products = productService.getAllInStock();
//
//        if ("ROLE_ADMIN".equals(role)) {
//            return ResponseEntity.ok().body(products); // Admin sees all fields
//        } else {
//            return ResponseEntity.ok().body(
//                    new ObjectMapper().writerWithView(Views.User.class).writeValueAsString(products)
//            ); // Users see limited fields
//        }
//    }

    @GetMapping("/all")
    public ResponseEntity<?> getProducts(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        String role = jwtUtil.extractRole(token.substring(7));

        List<Product> products;
        if ("ROLE_ADMIN".equals(role)) {
            products = productService.getAllProducts(); // Admin sees all products
        } else {
            products = productService.getAllInStock(); // Users see only in-stock products
        }

        if ("ROLE_ADMIN".equals(role)) {
            return ResponseEntity.ok(products); // Admin sees all fields
        } else {
            return ResponseEntity.ok(products);
//            return ResponseEntity.ok(
//                    new ObjectMapper().writerWithView(Views.User.class).writeValueAsString(products)
//            ); // Users see limited fields
        }
    }



    @GetMapping("/{id}")
    @JsonView(Views.User.class)
    public ResponseEntity<?> getProductDetails(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
    }

    @GetMapping("/frequent/3")
    public ResponseEntity<?> getTopFrequentProducts(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        try {
            List<Product> topProducts = orderService.getTopFrequentProducts(username, 3);
            return ResponseEntity.ok(topProducts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    //preauthorized
    @GetMapping("/recent/3")
    public ResponseEntity<?> getTopRecentProducts(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        try {
            List<Product> recentProducts = orderService.getTopRecentProducts(username, 3);
            return ResponseEntity.ok(recentProducts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @JsonView(Views.Admin.class)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updateRequest, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        String role = jwtUtil.extractRole(token);

        // Ensure only admin can perform this update
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can update products.");
        }

        try {
            System.out.println("Received update request: " + updateRequest); //debug

            Product updatedProduct = productService.updateProduct(id, updateRequest);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @JsonView(Views.Admin.class)
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest newProductRequest, @RequestHeader("Authorization") String token) {
        try {
            System.out.println("Raw token: " + token);

            //Validate and extract information
            String username = jwtUtil.extractUsername(token.substring(7));
            String role = jwtUtil.extractRole(token);

            System.out.println("Extracted username: " + username);
            System.out.println("Extracted role: " + role);

            // Ensure only admins can create products
            if (!"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can create products.");
            }

            // Convert DTO to Product
            Product newProduct = Product.builder()
                    .name(newProductRequest.getName())
                    .description(newProductRequest.getDescription())
                    .wholesalePrice(newProductRequest.getWholesalePrice())
                    .retailPrice(newProductRequest.getRetailPrice())
                    .quantity(newProductRequest.getQuantity())
                    .build();

            System.out.println("Converted product: " + newProduct);

            Product createdProduct = productService.createProduct(newProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (MalformedJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profit/3")
    public ResponseEntity<?> getTopProfitableProducts(@RequestHeader("Authorization") String token) {
        String role = jwtUtil.extractRole(token.substring(7));

        try {
            if (!"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can access this endpoint.");
            }

            List<Product> topProducts = productService.getTopProfitableProducts(3);
            return ResponseEntity.ok(topProducts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/popular/3")
    public ResponseEntity<?> getMostPopularProducts(@RequestHeader("Authorization") String token) {
        String role = jwtUtil.extractRole(token.substring(7));

        try {
            if (!"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admins can access this endpoint.");
            }

            List<Product> popularProducts = productService.getMostPopularProducts(3);
            return ResponseEntity.ok(popularProducts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/debug")
    public ResponseEntity<String> debugEndpoint() {
        System.out.println("Debug endpoint hit!");
        return ResponseEntity.ok("Debug endpoint works!");
    }
}
