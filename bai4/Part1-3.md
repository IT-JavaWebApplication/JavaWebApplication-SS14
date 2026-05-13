# HỆ THỐNG QUẢN LÝ GIỎ HÀNG TẠM VÀ ĐỒNG BỘ KHO

## 1. Bối cảnh & Yêu cầu bài toán

Hệ thống thương mại điện tử Rikkei Mall đang gặp vấn đề về việc giữ hàng trong quá trình khách hàng thanh toán.

Khi khách hàng thêm sản phẩm vào giỏ hàng và thực hiện Checkout, hệ thống cần tạm thời giữ số lượng sản phẩm đó trong 
vòng 15 phút để tránh tình trạng nhiều người cùng mua vượt quá số lượng tồn kho.

### Quy tắc nghiệp vụ

* Khi khách hàng Checkout:

    * Hệ thống tạo đơn hàng trạng thái `PENDING`
    * Tạm giữ số lượng sản phẩm trong kho

* Nếu khách hàng thanh toán thành công trong vòng 15 phút:

    * Đơn hàng chuyển sang trạng thái `PAID`
    * Số lượng tồn kho bị trừ vĩnh viễn

* Nếu khách hàng không thanh toán hoặc hủy đơn:

    * Đơn hàng chuyển sang `EXPIRED` hoặc `CANCELLED`
    * Hệ thống tự động hoàn lại số lượng đã giữ

---

# 2. Phân tích Input / Output

## 2.1 Input

### Checkout

| Dữ liệu   | Kiểu    |
| --------- | ------- |
| userId    | Long    |
| productId | Long    |
| quantity  | Integer |

### Payment

| Dữ liệu | Kiểu |
| ------- | ---- |
| orderId | Long |

### Scheduler

| Dữ liệu     | Kiểu          |
| ----------- | ------------- |
| currentTime | LocalDateTime |

---

## 2.2 Output

### Thành công

| Kết quả           | Ý nghĩa             |
| ----------------- | ------------------- |
| Tạo Order Pending | Giữ hàng thành công |
| Payment Success   | Trừ kho thành công  |
| Release Success   | Hoàn kho thành công |

### Thất bại

| Lỗi                     | Nguyên nhân            |
| ----------------------- | ---------------------- |
| OutOfStockException     | Không đủ hàng          |
| ProductDeletedException | Sản phẩm đã bị xóa     |
| OrderExpiredException   | Đơn hàng hết hạn       |
| SessionTimeoutException | Phiên làm việc hết hạn |

---

# 3. Ràng buộc & Bẫy dữ liệu

## 3.1 Long-running Transaction

Nếu sử dụng transaction kéo dài trong suốt quá trình khách hàng thanh toán sẽ gây:

* Database lock kéo dài
* Deadlock
* Giảm hiệu năng hệ thống
* Treo transaction

Do đó hệ thống chỉ nên dùng transaction ngắn.

---

## 3.2 Product bị xóa khi đang giữ hàng

Nếu sản phẩm đã bị xóa khỏi danh mục nhưng vẫn còn đơn `PENDING`, hệ thống cần:

* Không xóa cứng dữ liệu
* Dùng Soft Delete
* Vẫn cho phép release reserved stock

Ví dụ:

```java
private Boolean deleted = false;
```

---

## 3.3 Session Timeout

Không được lưu dữ liệu reservation trong session.

Thông tin phải lưu trong database:

* Order
* OrderItem
* Reservation

để tránh mất dữ liệu khi session timeout.

---

## 3.4 Race Condition

Nếu nhiều người checkout cùng lúc có thể xảy ra:

* Overselling
* Stock âm

Cần xử lý bằng:

* Transaction
* Locking
* Isolation Level
* Reserved Stock

---

# 4. Giải pháp 1 — Database Transaction + Pending Order

## 4.1 Ý tưởng

Sử dụng transaction ngắn để:

1. Kiểm tra tồn kho
2. Tăng reserved stock
3. Tạo order trạng thái `PENDING`
4. Commit ngay

Sau đó hệ thống scheduler sẽ xử lý các đơn hết hạn.

---

## 4.2 Thiết kế dữ liệu

### Product

| Field         | Ý nghĩa       |
| ------------- | ------------- |
| stock         | Tổng tồn kho  |
| reservedStock | Hàng đang giữ |
| deleted       | Soft delete   |

### Order

| Field     | Ý nghĩa                  |
| --------- | ------------------------ |
| status    | PENDING / PAID / EXPIRED |
| expiredAt | Thời gian hết hạn        |

---

## 4.3 Flow Checkout

```text
BEGIN TRANSACTION

1. Lock product
2. Kiểm tra stock
3. reservedStock += quantity
4. Tạo order PENDING
5. Commit

END
```

---

## 4.4 Flow Payment

```text
BEGIN TRANSACTION

1. Tìm order
2. Kiểm tra chưa hết hạn
3. stock -= quantity
4. reservedStock -= quantity
5. status = PAID

COMMIT
```

---

## 4.5 Flow Release

```text
BEGIN TRANSACTION

1. Scheduler quét order quá hạn
2. reservedStock -= quantity
3. status = EXPIRED

COMMIT
```

---

## 4.6 Ưu điểm

* Dữ liệu an toàn
* Tránh deadlock
* Transaction ngắn
* Dễ scale
* Dễ maintain

---

## 4.7 Nhược điểm

* Cần scheduler
* Nhiều trạng thái order

---

# 5. Giải pháp 2 — Hibernate Interceptor / Scheduled Task

## 5.1 Ý tưởng

Sử dụng Interceptor hoặc Event để tự động xử lý cập nhật tồn kho.

Scheduler sẽ quét định kỳ và release các reservation quá hạn.

---

## 5.2 Flow

```text
Checkout
→ Tạo Reservation
→ Scheduler quét timeout
→ Release stock
```

---

## 5.3 Ưu điểm

* Logic tách biệt
* Dễ tích hợp event-driven

---

## 5.4 Nhược điểm

* Khó debug
* Khó bảo trì
* Dễ phát sinh side-effect
* Khó kiểm soát transaction

---

# 6. So sánh hai giải pháp

| Tiêu chí           | Giải pháp 1 | Giải pháp 2 |
| ------------------ | ----------- | ----------- |
| Tốc độ xử lý       | Nhanh       | Trung bình  |
| Độ an toàn dữ liệu | Cao         | Trung bình  |
| Deadlock           | Thấp        | Trung bình  |
| Dễ bảo trì         | Cao         | Khó         |
| Memory             | Tốt         | Tốn hơn     |
| Khả năng scale     | Tốt         | Trung bình  |
| Debug              | Dễ          | Khó         |

---

# 7. Lựa chọn giải pháp

## Giải pháp được chọn

Database Transaction + Pending Order + Scheduled Task

---

## Lý do lựa chọn

* Transaction ngắn
* Không lock DB lâu
* Đảm bảo ACID
* Giảm deadlock
* Phù hợp hệ thống ecommerce lớn
* Dễ mở rộng microservice
* Dễ maintain code

---

# 8. Thiết kế Flowchart

```text
Khách Checkout
        |
        v
Kiểm tra tồn kho
        |
        v
Đủ hàng ?
   /        \
 Không       Có
  |           |
Thông báo   Tạo Order Pending
              |
              v
      reservedStock += qty
              |
              v
       Chờ thanh toán
         /        \
     Thành công   Timeout
        |             |
        v             v
 stock -= qty   release reserved
 status=PAID    status=EXPIRED
```

---

# 9. Kết luận

Giải pháp sử dụng:

* Pending Order
* Reserved Stock
* Scheduled Release
* Transaction ngắn

là giải pháp tối ưu cho hệ thống thương mại điện tử có lượng truy cập lớn.

Giải pháp này giúp:

* Đảm bảo dữ liệu tồn kho chính xác
* Tránh overselling
* Hạn chế deadlock
* Tăng hiệu năng hệ thống
* Dễ mở rộng và bảo trì
