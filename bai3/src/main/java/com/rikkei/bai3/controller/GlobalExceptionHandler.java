package com.rikkei.bai3.controller;

import com.rikkei.bai3.service.OutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.rikkei.bai3.service.OutOfStockException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi hết hàng (Lỗi bạn vừa tạo ở trên)
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<String> handleOutOfStock(OutOfStockException ex) {
        // Bây giờ ex.getMessage() sẽ hoạt động vì ex kế thừa từ RuntimeException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Xử lý lỗi tranh chấp dữ liệu (Pessimistic Lock Timeout)
    @ExceptionHandler(org.springframework.dao.PessimisticLockingFailureException.class)
    public ResponseEntity<String> handleLockingFailure(Exception ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Hệ thống đang bận do lượng truy cập quá lớn, vui lòng thử lại sau!");
    }
}