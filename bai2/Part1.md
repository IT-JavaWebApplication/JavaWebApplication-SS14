# Phần 1 - Phân tích

Nếu không sử dụng Transaction, các câu lệnh cập nhật dữ liệu trong Hibernate/JDBC sẽ được thực thi riêng lẻ. Điều này 
dẫn đến tình trạng một phần dữ liệu đã được lưu xuống database dù toàn bộ nghiệp vụ chưa hoàn thành.

Trong đoạn code hiện tại:

```java
// Bước 1: Hủy đơn hàng
order.setStatus("CANCELLED");
session.update(order);

// Bước 2: Hoàn kho
Product product = session.get(Product.class, order.getProductId());
product.setStock(product.getStock() + order.getQuantity());
session.update(product);