package com.rikkei.bai4.controller;

import com.rikkei.bai4.entity.Order;
import com.rikkei.bai4.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public Order checkout(
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) {

        return orderService.checkout(productId, quantity);
    }

    @PostMapping("/payment/{orderId}")
    public String payment(
            @PathVariable Long orderId
    ) {

        return orderService.payment(orderId);
    }
}