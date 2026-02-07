package com.beaconfire.shoppy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private String name;
    private String description;
    private Double wholesalePrice;
    private Double retailPrice;
    private Integer quantity;
}