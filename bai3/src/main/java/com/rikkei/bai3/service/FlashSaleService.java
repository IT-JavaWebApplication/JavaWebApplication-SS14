package com.rikkei.bai3.service;

import com.rikkei.bai3.entity.Product;
import com.rikkei.bai3.entity.Order;
import com.rikkei.bai3.repository.OrderRepository;
import com.rikkei.bai3.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void placeOrder(Long productId, Long userId) {
        // 1. Đọc và khóa dòng dữ liệu (Pessimistic Lock)
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // 2. Kiểm tra kho
        if (product.getStock() < 1) {
            throw new OutOfStockException("Hết hàng rồi bạn ơi!");
        }

        // 3. Khấu trừ tồn kho
        product.setStock(product.getStock() - 1);
        productRepository.save(product);

        // 4. Tạo đơn hàng
        Order order = new Order(userId, productId, "SUCCESS");
        orderRepository.save(order);
    }
}