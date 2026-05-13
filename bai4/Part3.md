# 3. SO SÁNH VÀ LỰA CHỌN

| Tiêu chí        | Giải pháp 1 | Giải pháp 2 |
| --------------- | ----------- | ----------- |
| Tốc độ          | Nhanh       | Trung bình  |
| An toàn dữ liệu | Cao         | Trung bình  |
| Deadlock        | Thấp        | Trung bình  |
| Dễ bảo trì      | Cao         | Khó         |
| Khả năng scale  | Tốt         | Trung bình  |

---

# Giải pháp được chọn

Transaction + Pending Order + Scheduler

---

# Lý do lựa chọn

* Transaction ngắn
* Không lock DB lâu
* Giảm deadlock
* Dễ mở rộng
* Dễ maintain
* Phù hợp ecommerce lớn
