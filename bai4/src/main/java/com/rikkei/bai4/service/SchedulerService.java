package com.rikkei.bai4.service;

import com.rikkei.bai4.entity.Order;
import com.rikkei.bai4.entity.OrderStatus;
import com.rikkei.bai4.entity.Product;
import com.rikkei.bai4.repository.OrderRepository;
import com.rikkei.bai4.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredOrders() {

        List<Order> expiredOrders =
                orderRepository.findByStatusAndExpiredAtBefore(
                        OrderStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Order order : expiredOrders) {

            Product product = productRepository
                    .findById(order.getProductId())
                    .orElse(null);

            if (product != null && !product.getDeleted()) {

                product.setReservedStock(
                        product.getReservedStock() - order.getQuantity()
                );

                productRepository.save(product);
            }

            order.setStatus(OrderStatus.EXPIRED);

            orderRepository.save(order);

            log.info("Released order id = {}", order.getId());
        }
    }
}