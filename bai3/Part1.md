Phần 1: Báo cáo phân tích và thiết kế giải pháp
1. Phân tích bài toán (I/O)
   Input: productId (ID sản phẩm iPhone 15), quantity (thường là 1), userId.

Output: * Thành công: Đơn hàng được tạo, tồn kho giảm.

Thất bại: Thông báo "Hết hàng" hoặc "Hệ thống đang bận" (nếu có tranh chấp).

2. Đề xuất giải pháp
   Trong bối cảnh Flash Sale (tốc độ cao, tranh chấp cực lớn), chúng ta có hai hướng tiếp cận chính:

Giải pháp 1: Optimistic Locking (@Version)

Cơ chế: Dùng một cột version trong DB. Khi Update, Hibernate sẽ check: WHERE id = ? AND version = current_version.

Ưu điểm: Hiệu năng cực cao vì không khóa DB (Non-blocking).

Nhược điểm: Nếu hàng nghìn người cùng nhấn một lúc, tỷ lệ bị lỗi OptimisticLockException rất cao (chỉ 1 người thắng, 
999 người thua).

Giải pháp 2: Pessimistic Locking (PESSIMISTIC_WRITE)

Cơ chế: Khi Thread A đọc dữ liệu để check kho, nó sẽ thực hiện lệnh SELECT ... FOR UPDATE. Các Thread khác muốn đọc/ghi 
dòng đó phải đợi Thread A kết thúc.

Ưu điểm: Đảm bảo tính nhất quán tuyệt đối, không bao giờ bán lố, phù hợp với số lượng hàng ít (chỉ 5 chiếc).

Lựa chọn: Với số lượng hàng cực ít (5 chiếc), Pessimistic Lock thường được ưu tiên để đảm bảo ai "xếp hàng" trước thì 
mua được trước.

3. Thiết kế các bước (Flowchart)
   Bắt đầu Transaction.

Truy vấn Product: Sử dụng PESSIMISTIC_WRITE để khóa dòng sản phẩm lại.

Kiểm tra điều kiện: Nếu stock < quantity -> Throw Exception "Hết hàng".

Xử lý:

Trừ stock trong đối tượng Product.

Lưu (Update) Product.

Tạo bản ghi Order mới.

Commit Transaction: Khóa được giải phóng.