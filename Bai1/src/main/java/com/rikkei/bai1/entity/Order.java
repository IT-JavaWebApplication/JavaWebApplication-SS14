package com.rikkei.bai1.entity;

import jakarta.persistence.*;

import jakarta.persistence.*;

@Entity
@Table(name = "orders") // Tên bảng trong DB
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    private String status; // Ví dụ: "PENDING", "PAID"

    // Constructors
    public Order() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}