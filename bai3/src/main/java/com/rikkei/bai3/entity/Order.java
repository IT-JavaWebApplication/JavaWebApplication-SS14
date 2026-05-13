package com.rikkei.bai3.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private String status;

    public Order(Long userId, Long productId, String status) {
        this.userId = userId;
        this.productId = productId;
        this.status = status;
    }
}