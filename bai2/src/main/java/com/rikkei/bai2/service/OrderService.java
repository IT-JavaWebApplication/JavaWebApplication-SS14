package com.rikkei.bai2.service;

import com.rikkei.bai2.config.HibernateUtils;
import com.rikkei.bai2.entity.Order;
import com.rikkei.bai2.entity.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OrderService {

    public void cancelOrder(Long orderId) {

        Session session = HibernateUtils
                .getSessionFactory()
                .openSession();

        Transaction tx = null;

        try {

            tx = session.beginTransaction();

            Order order = session.find(Order.class, orderId);

            if (order == null) {
                throw new Exception("Đơn hàng không tồn tại!");
            }

            order.setStatus("CANCELLED");
            session.persist(order);

            Product product = session.find(
                    Product.class,
                    order.getProductId()
            );

            if (product == null) {
                throw new Exception("Sản phẩm không tồn tại!");
            }

            product.setStock(
                    product.getStock() + order.getQuantity()
            );

            session.persist(product);

            tx.commit();

            System.out.println("Hủy đơn thành công!");

        } catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }

            System.out.println("Lỗi: " + e.getMessage());

        } finally {

            session.close();
        }
    }
}