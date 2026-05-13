Phần 1: Phân tích logic & Truy vết lỗi (Trace Code)
1. Tại sao dữ liệu bị sai?
   Hãy nhìn vào luồng thực thi (execution flow) của đoạn code cũ:

Bước 1: session.get(Order.class, orderId) lấy đơn hàng lên.

Bước 2: order.setStatus("PAID") thay đổi trạng thái trong bộ nhớ.

Bước 3: session.update(order) đánh dấu đơn hàng này cần được cập nhật vào DB.

Bước 4: Lệnh if (true) throw new RuntimeException(...) kích hoạt. Chương trình ngay lập tức nhảy xuống khối catch.

Bước 5: Khối catch chỉ in ra thông báo lỗi mà không có hành động khôi phục dữ liệu.

Bước 6: Khối finally đóng session.

Vấn đề cốt lõi: Trong Hibernate, nếu không cấu hình chặt chẽ, khi session.close() được gọi hoặc khi flush() vô tình xảy 
ra, những thay đổi đã thực hiện ở Bước 3 có thể đã được đẩy xuống Database (tùy thuộc vào chế độ Auto-commit của JDBC 
hoặc cấu hình FlushMode). Tuy nhiên, vì chương trình bị "gãy" ở giữa, phần trừ tiền ví (Wallet) chưa bao giờ được chạy. 
Kết quả: Khách được nhận hàng mà không mất tiền.

2. Các lệnh Transaction đang bị thiếu
   Đoạn code trên hoàn toàn thiếu việc quản lý Transaction (Giao dịch). Để đảm bảo "Tất cả cùng thành công hoặc tất cả 
cùng thất bại", chúng ta thiếu 3 lệnh quan trọng:

session.beginTransaction(): Để đánh dấu điểm bắt đầu của một đơn vị công việc.

transaction.commit(): Để xác nhận và lưu vĩnh viễn tất cả thay đổi vào DB nếu mọi thứ suôn sẻ.

transaction.rollback(): Để "quay ngược thời gian", hủy bỏ các lệnh đã chạy trước đó nếu có bất kỳ lỗi nào xảy ra giữa 
chừng.