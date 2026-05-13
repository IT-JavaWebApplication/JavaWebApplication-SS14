package com.rikkei.bai1.service;

import com.rikkei.bai1.entity.Order;
import com.rikkei.bai1.entity.Wallet;
import com.rikkei.bai1.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PaymentService {

    public void processPayment(Long orderId, Long walletId, double totalAmount) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            // 1. Bắt đầu giao dịch (Transaction)
            transaction = session.beginTransaction();

            // 2. Cập nhật trạng thái đơn hàng
            Order order = session.find(Order.class, orderId);
            if (order == null) throw new Exception("Không tìm thấy đơn hàng");

            order.setStatus("PAID");
            session.persist(order);

            // Giả lập lỗi hệ thống bất ngờ
            if (true) throw new RuntimeException("Kết nối đến cổng thanh toán thất bại!");

            // 3. Trừ tiền trong ví khách hàng
            Wallet wallet = session.find(Wallet.class, walletId);
            if (wallet == null) throw new Exception("Không tìm thấy ví");

            if (wallet.getBalance() < totalAmount) {
                throw new RuntimeException("Số dư không đủ để thanh toán!");
            }

            wallet.setBalance(wallet.getBalance() - totalAmount);
            session.persist(wallet);

            // 4. Nếu mọi thứ tốt đẹp, xác nhận thay đổi vào DB
            transaction.commit();
            System.out.println("Thanh toán thành công!");

        } catch (Exception e) {
            // 5. Nếu có lỗi, lập tức Rollback để khôi phục trạng thái ban đầu
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Lỗi hệ thống: " + e.getMessage());
            System.err.println("Giao dịch đã được hủy bỏ (Rollback). Dữ liệu vẫn an toàn.");

        } finally {
            // 6. Luôn đóng session để giải phóng tài nguyên
            session.close();
        }
    }
}