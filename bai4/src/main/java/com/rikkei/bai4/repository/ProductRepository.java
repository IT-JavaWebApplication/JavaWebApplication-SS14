package com.rikkei.bai4.repository;

import com.rikkei.bai4.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}