package com.beaconfire.shoppy.controller;

//import com.beaconfire.onlineshopping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.beaconfire.shoppy.exception.ProductNotFoundException;
import com.beaconfire.shoppy.model.Product;
import com.beaconfire.shoppy.model.Watchlist;
import com.beaconfire.shoppy.service.WatchlistService;

//import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

//    @Autowired
//    private JwtUtil jwtUtil;

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> addProductToWatchList(@PathVariable Long productId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Product addedProduct = watchlistService.addProductToWatchList(productId, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product '" + addedProduct.getName() + "' added to your watchlist successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<String> removeProductFromWatchlist(@PathVariable Long productId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = watchlistService.removeProductFromWatchlist(username, productId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/products/all")
    public ResponseEntity<?> getWatchlist() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Watchlist> watchlist = watchlistService.getWatchlistByUser(username);
        return ResponseEntity.ok(watchlist);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
