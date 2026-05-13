4. FLOWCHART

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

