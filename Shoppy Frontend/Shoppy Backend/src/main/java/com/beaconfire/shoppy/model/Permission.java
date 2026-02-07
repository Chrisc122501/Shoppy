package com.beaconfire.shoppy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Entity
@Table(name = "Permission")
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

//    @Column(name = "value", nullable = false)
//    private String value;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @ManyToMany(mappedBy = "permissions")
    private List<User> users; // Many-to-Many relationship
}