 1. PHÂN TÍCH YÊU CẦU

 1.1 Bối cảnh

Rikkei Mall gặp vấn đề overselling khi nhiều người dùng cùng mua một sản phẩm tại cùng thời điểm.

Khi khách hàng checkout, hệ thống cần giữ hàng tạm thời trong 15 phút.

Nếu khách thanh toán thành công:

Trừ kho vĩnh viễn

Nếu khách không thanh toán:

Hoàn lại số lượng sản phẩm



1.2 Yêu cầu nghiệp vụ

 Checkout

Kiểm tra tồn kho
Tạo order PENDING
Tăng reserved stock

 Payment

Kiểm tra order còn hiệu lực
Trừ kho chính thức
Giảm reserved stock

 Release Stock

Scheduler quét order hết hạn
Hoàn kho
Chuyển trạng thái EXPIRED



 1.3 Input / Output

 Input

| Dữ liệu   | Kiểu    |
| --------- | ------- |
| productId | Long    |
| quantity  | Integer |
| orderId   | Long    |



 Output

| Kết quả          | Ý nghĩa               |
| ---------------- | --------------------- |
| Checkout success | Giữ hàng thành công   |
| Payment success  | Thanh toán thành công |
| Release success  | Hoàn kho thành công   |


1.4 Các vấn đề cần xử lý

Long-running transaction
Deadlock
Overselling
Session timeout
Product deleted
Race condition
