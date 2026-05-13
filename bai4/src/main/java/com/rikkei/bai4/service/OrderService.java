package com.rikkei.bai4.service;

import com.rikkei.bai4.entity.*;
import com.rikkei.bai4.exception.OrderExpiredException;
import com.rikkei.bai4.exception.OutOfStockException;
import com.rikkei.bai4.repository.OrderRepository;
import com.rikkei.bai4.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    @Transactional
    public Order checkout(Long productId, Integer quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getDeleted()) {
            throw new RuntimeException("Product deleted");
        }

        int availableStock =
                product.getStock() - product.getReservedStock();

        if (availableStock < quantity) {
            throw new OutOfStockException("Not enough stock");
        }

        product.setReservedStock(
                product.getReservedStock() + quantity
        );

        productRepository.save(product);

        Order order = new Order();

        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.PENDING);

        order.setCreatedAt(LocalDateTime.now());

        order.setExpiredAt(
                LocalDateTime.now().plusMinutes(15)
        );

        return orderRepository.save(order);
    }

    @Transactional
    public String payment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order invalid");
        }

        if (order.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new OrderExpiredException("Order expired");
        }

        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(
                product.getStock() - order.getQuantity()
        );

        product.setReservedStock(
                product.getReservedStock() - order.getQuantity()
        );

        productRepository.save(product);

        order.setStatus(OrderStatus.PAID);

        orderRepository.save(order);

        return "Payment success";
    }
}