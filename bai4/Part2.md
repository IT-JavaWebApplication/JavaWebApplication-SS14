2. ĐỀ XUẤT GIẢI PHÁP

Giải pháp 1 — Transaction + Pending Order

 Ý tưởng

Sử dụng transaction ngắn:

1. Kiểm tra stock
2. Reserved stock
3. Tạo order PENDING
4. Commit ngay

Scheduler sẽ xử lý các đơn quá hạn.



 Ưu điểm

Dữ liệu an toàn
Tránh deadlock
Hiệu năng tốt
Dễ maintain


 Nhược điểm

Nhiều trạng thái order
Cần scheduler



Giải pháp 2 — Hibernate Interceptor
Ý tưởng

Dùng Interceptor hoặc Event để tự động xử lý tồn kho.


 Ưu điểm

Tách biệt logic
Hỗ trợ event-driven



 Nhược điểm

Khó debug
Dễ side-effect
Khó maintain
