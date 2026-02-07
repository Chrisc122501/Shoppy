package com.beaconfire.shoppy.model;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.beaconfire.shoppy.util.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "Product")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @JsonView(Views.User.class)
    private Long productId;

    @Column(name = "description")
    @JsonView(Views.User.class)
    private String description;

    @Column(name = "name", nullable = false)
    @JsonView(Views.User.class)
    private String name;

    //@JsonIgnore
    @Column(name = "quantity", nullable = false)
    @JsonView(Views.User.class)
    private Integer quantity;

    @Column(name = "retail_price", nullable = false)
    @JsonView(Views.User.class)
    private Double retailPrice;

    @Column(name = "wholesale_price", nullable = false)
    @JsonView(Views.Admin.class)
    private Double wholesalePrice;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Watchlist> watchlist;
}