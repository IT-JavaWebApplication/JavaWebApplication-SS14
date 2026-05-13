package com.rikkei.bai4.repository;

import com.rikkei.bai4.entity.Order;
import com.rikkei.bai4.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusAndExpiredAtBefore(
            OrderStatus status,
            LocalDateTime time
    );
}