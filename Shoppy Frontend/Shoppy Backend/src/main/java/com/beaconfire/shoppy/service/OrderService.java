package com.beaconfire.shoppy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beaconfire.shoppy.dto.OrderItemDTO;
import com.beaconfire.shoppy.exception.NotEnoughInventoryException;
import com.beaconfire.shoppy.model.CustomerOrder;
import com.beaconfire.shoppy.model.OrderItem;
import com.beaconfire.shoppy.model.Product;
import com.beaconfire.shoppy.model.User;
import com.beaconfire.shoppy.repository.OrderRepository;
import com.beaconfire.shoppy.repository.ProductRepository;
import com.beaconfire.shoppy.repository.UserRepository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CustomerOrder placeOrder(String username, List<OrderItemDTO> orderItems) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUser(user);
        order.setOrderStatus("Processing");
        order.setDatePlaced(new Timestamp(System.currentTimeMillis()));

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDTO itemDTO : orderItems) {
            Product product = productRepository.findById(itemDTO.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("Product with ID " + itemDTO.getProductId() + " not found.");
            }

            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new NotEnoughInventoryException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
            //productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPurchasedPrice(product.getRetailPrice() * itemDTO.getQuantity());
            orderItem.setWholesalePrice(product.getWholesalePrice());
            orderItem.setOrder(order);

            items.add(orderItem);
        }

        order.setOrderItems(items);
        //added
        CustomerOrder savedOrder = orderRepository.save(order);
        for (OrderItem item : savedOrder.getOrderItems()) {
            productRepository.save(item.getProduct());
        }
        //return orderRepository.save(order);
        return savedOrder;
    }

//    public List<CustomerOrder> getAllOrders(String username) {
//        User user = userRepository.findByUsername(username);
//        if (user == null) {
//            throw new IllegalArgumentException("User not found.");
//        }
//        return orderRepository.findAllByUserId(user.getUserId());
//    }

    public List<CustomerOrder> getAllOrders(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        List<CustomerOrder> orders = orderRepository.findAllByUserId(user.getUserId());
        for (CustomerOrder order : orders) {
            // Force lazy collections to initialize
            order.getOrderItems().size();
        }
        return orders;
    }


    public CustomerOrder getOrderById(Long id, String username) {
        CustomerOrder order = orderRepository.findById(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !order.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to view this order.");
        }

        order.getOrderItems().forEach(item -> {
            item.getProduct().getName(); // Force initialization of product
            item.getProduct().getRetailPrice(); // Ensure retail price is accessible
        });

        return order;
    }

    @Transactional
    public CustomerOrder cancelOrder(Long id, String username) {
        CustomerOrder order = getOrderById(id, username);
        if (!"Processing".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Only orders in Processing status can be canceled.");
        }
        order.setOrderStatus("Canceled");
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity()); // Increment stock
            productRepository.save(product);
        }
        return orderRepository.save(order);
    }

    public List<Product> getTopFrequentProducts(String username, int limit) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        List<OrderItem> orderItems = orderRepository.findOrderItemsByUserAndStatus(user.getUserId(), "Completed");

        // Count the frequency of each product in the user's order history
        Map<Product, Long> productFrequency = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProduct, Collectors.counting()));

        // Sort products by frequency and item ID as a tiebreaker, and limit to the top 'limit' items
        return productFrequency.entrySet().stream()
                .sorted((e1, e2) -> {
                    int frequencyComparison = e2.getValue().compareTo(e1.getValue());
                    if (frequencyComparison != 0) {
                        return frequencyComparison;
                    } else {
                        return e1.getKey().getProductId().compareTo(e2.getKey().getProductId());
                    }
                })
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Product> getTopRecentProducts(String username, int limit) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        List<OrderItem> orderItems = orderRepository.findOrderItemsByUserAndStatus(user.getUserId(), "Completed");

        // Sort items by purchase date in descending order (most recent first), using item ID as a tiebreaker
        return orderItems.stream()
                .sorted((item1, item2) -> {
                    int dateComparison = item2.getOrder().getDatePlaced().compareTo(item1.getOrder().getDatePlaced());
                    if (dateComparison != 0) {
                        return dateComparison;
                    } else {
                        return item1.getProduct().getProductId().compareTo(item2.getProduct().getProductId());
                    }
                })
                .map(OrderItem::getProduct)
                .distinct() // Ensure unique products
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<CustomerOrder> getAllOrdersForAdmin() {
        return orderRepository.findAll();
    }

    public CustomerOrder getOrderByIdForAdmin(Long id) {
        CustomerOrder order = orderRepository.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }
        return order;
    }

    public CustomerOrder getOrderByIdForUser(Long id, String username) {
        CustomerOrder order = orderRepository.findById(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null || !order.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to view this order.");
        }

        return order;
    }

    @Transactional
    public CustomerOrder cancelOrderForAdmin(Long id) {
        CustomerOrder order = orderRepository.findById(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }

        if (!"Processing".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Only orders in Processing status can be canceled.");
        }

        order.setOrderStatus("Canceled");

        // Restore stock for each item in the canceled order
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public CustomerOrder completeOrderForAdmin(Long id) {
        CustomerOrder order = orderRepository.findById(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found.");
        }

        if (!"Processing".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("Only orders in Processing status can be completed.");
        }

        order.setOrderStatus("Completed");
        return orderRepository.save(order);
    }
}
