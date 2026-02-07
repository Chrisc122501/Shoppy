package com.beaconfire.shoppy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beaconfire.shoppy.exception.ProductNotFoundException;
import com.beaconfire.shoppy.model.Product;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.model.Watchlist;
import com.beaconfire.shoppy.model.WatchlistId;
import com.beaconfire.shoppy.repository.ProductRepository;
import com.beaconfire.shoppy.repository.UserRepository;
import com.beaconfire.shoppy.repository.WatchlistRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Product addProductToWatchList(Long productId, String username) {
        User user = userRepository.findByUsername(username);
        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " does not exist.");
        }

        WatchlistId id = new WatchlistId(user.getUserId(), productId);
        Watchlist watchlistItem = watchlistRepository.findById(id);

        if (watchlistItem != null) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' is already in the watchlist.");
        }

        watchlistItem = new Watchlist();
        watchlistItem.setId(id);
        watchlistItem.setUser(user);
        watchlistItem.setProduct(product);
        watchlistRepository.save(watchlistItem);

        // Return the product that was added to the watchlist
        return product;
    }

    @Transactional
    public String removeProductFromWatchlist(String username, Long productId) {
        User user = userRepository.findByUsername(username);
        WatchlistId id = new WatchlistId(user.getUserId(), productId);
        Watchlist watchlist = watchlistRepository.findById(id);

        if (watchlist == null) {
            return "Product is not on your watchlist.";
        }

        Product product = productRepository.findById(productId);
        watchlistRepository.delete(watchlist);
        return product.getName() + "removed from your watchlist.";
    }

    public List<Watchlist> getWatchlistByUser(String username) {
        User user = userRepository.findByUsername(username);
        return watchlistRepository.findAllByUserId(user.getUserId());
    }
}
