package com.beaconfire.shoppy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beaconfire.shoppy.model.OrderItem;
import com.beaconfire.shoppy.model.Product;
import com.beaconfire.shoppy.repository.OrderRepository;
import com.beaconfire.shoppy.repository.ProductRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<Product> getAllInStock() {
        return productRepository.findAllInStock();
    }

    public List<Product> getAllProducts() {
        int a = Math.max(1,6);
        return productRepository.findAll();
    }

    @Cacheable("products")
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id);
        if (product != null && product.getQuantity() > 0) {
            return product;
        } else {
            throw new IllegalArgumentException("Product not found or out of stock");
        }
    }

    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product updateRequest) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        // Only update fields that are allowed (not the name)

        if (updateRequest.getDescription() != null) {
            product.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getWholesalePrice() != null) {
            product.setWholesalePrice(updateRequest.getWholesalePrice());
        }
        if (updateRequest.getRetailPrice() != null) {
            product.setRetailPrice(updateRequest.getRetailPrice());
        }
        if (updateRequest.getQuantity() != null) {
            product.setQuantity(updateRequest.getQuantity());
        }

        productRepository.save(product);
        return product;
    }

    public Product createProduct(Product newProduct) {
        // Check if a product with the same name already exists
        System.out.println("Checking if product already exists with name: " + newProduct.getName());

        Product existingProduct = productRepository.findByName(newProduct.getName());
        if (existingProduct != null) {
            throw new IllegalArgumentException("A product with the name '" + newProduct.getName() + "' already exists.");
        }

        System.out.println("Saving new product: " + newProduct);

        productRepository.save(newProduct);
        return newProduct;
    }

    public List<Product> getTopProfitableProducts(int limit) {
        List<OrderItem> completedOrderItems = orderRepository.findOrderItemsByStatus("Completed");

        completedOrderItems.forEach(orderItem -> {
            System.out.println("OrderItem: " + orderItem.getProduct().getName() + ", Quantity: "
                    + orderItem.getQuantity() + ", PurchasedPrice: " + orderItem.getPurchasedPrice());
        });

        //revenue for each product
        Map<Product, Double> productRevenue = completedOrderItems.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        //Collectors.summingDouble(oi -> oi.getPurchasedPrice())
                        Collectors.summingDouble(oi -> oi.getQuantity() * oi.getPurchasedPrice())
                ));

        productRevenue.forEach((product, revenue) -> {
            System.out.println("Product: " + product.getName() + ", Revenue: " + revenue);
        });

        //Sort products by revenue in descending order and limit to top 'limit'
        return productRevenue.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Product> getMostPopularProducts(int limit) {
        List<OrderItem> completedOrderItems = orderRepository.findCompletedOrderItems();

        //total quantity sold for each product
        Map<Product, Integer> productQuantitySold = completedOrderItems.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        //Sort products by quantity sold in descending order and limit to top 'limit'
        return productQuantitySold.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}