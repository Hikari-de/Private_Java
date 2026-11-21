# README 2: Thư mục `service/`

`service/` chứa **luật của cửa hàng**: thêm hàng thế nào, đặt đơn thế nào, trừ kho khi nào, tính
doanh thu ra sao. Quy tắc quan trọng nhất của thư mục này:

> Code trong `service/` **không dùng `Scanner` và không hỏi người dùng**. Nó nhận dữ liệu qua tham số,
> trả kết quả qua giá trị trả về hoặc ném lỗi. Việc in và đọc bàn phím là của các menu.

Lý do: cả `AdminMenu` và `CustomerMenu` cùng cần "tạo đơn", "hủy đơn". Luật nằm một chỗ ở service thì
hai menu cùng gọi, không phải viết hai lần và không thể làm sai luật của nhau.

## 1. Danh sách file

| File | Loại | Việc |
|---|---|---|
| ProductService / ProductServiceImpl | interface + class | Quản lý danh sách hàng |
| OrderService / OrderServiceImpl | interface + class | Tạo, trả, hủy đơn |
| PaymentService | interface | Hợp đồng chung của mọi cổng thanh toán |
| MoMoPaymentService, BankPaymentService | class | Hai cách trả tiền |
| ReportService | class | Tính số liệu báo cáo |

## 2. Kiến thức Java được dùng

| Kiến thức | Dùng ở đâu | Ý nghĩa ngắn |
|---|---|---|
| `interface` + `implements` | Product/Order/PaymentService | Interface nói "làm được gì", class nói "làm thế nào" |
| Đa hình qua interface | `paymentService.pay(order)` | Cùng một lời gọi, mỗi cổng thanh toán chạy khác nhau |
| `ArrayList` | ProductServiceImpl, OrderServiceImpl | Danh sách trong bộ nhớ |
| Ném và bắt ngoại lệ | Mọi service | `throw new IllegalArgumentException(...)` báo lỗi nghiệp vụ |
| Vòng lặp `for` + `if` | Mọi nơi | Duyệt danh sách để tìm, lọc, cộng dồn |
| `instanceof` kiểu mới | ReportService | Kiểm tra loại và ép kiểu trong một câu lệnh |
| Biến đếm tăng dần `nextId++` | OrderServiceImpl | Sinh mã đơn `ORD1`, `ORD2`... |
| Tách trách nhiệm | Toàn thư mục | Service tính toán, menu hiển thị |

## 3. Quy ước báo lỗi

Khi vi phạm luật, service **ném** `IllegalArgumentException` với câu thông báo:

```java
throw new IllegalArgumentException("Product ID already exists.");
```

Menu bắt lại bằng `try/catch` và in `e.getMessage()`. Nhờ vậy service không cần biết giao diện
là console hay cửa sổ.

## 4. `ProductService` và `ProductServiceImpl`

Interface khai báo 5 việc:

| Method | Mô tả |
|---|---|
| `addProduct(product)` | Thêm hàng. **Ném lỗi** nếu mã đã tồn tại |
| `removeProduct(id)` | Xóa hàng. Ném lỗi nếu không thấy |
| `findById(id)` | Tìm theo mã (không phân biệt hoa thường). Không thấy thì trả `null` |
| `getAll()` | Mọi sản phẩm. Trả **bản sao** của danh sách |
| `getAvailable(group)` | Chỉ hàng **bán được** (còn kho, chưa hết hạn). `group = null` là mọi nhóm |

`getAvailable` là chỗ khách được bảo vệ:

```java
boolean rightGroup = group == null || p.getGroup() == group;
if (rightGroup && p.isAvailable()) { result.add(p); }
```

Nó gọi `p.isAvailable()` nên hàng hết hạn hay hết kho tự biến mất khỏi mắt khách, mà service
không cần biết sản phẩm đó thuộc loại nào (nhờ đa hình trong `model`).

## 5. `OrderService` và `OrderServiceImpl`

### Các method

| Method | Mô tả |
|---|---|
| `checkout(customer, cart)` | Biến giỏ thành đơn, trừ kho |
| `getAll()`, `getByStatus(status)`, `getByPhone(phone)` | Các cách xem danh sách đơn |
| `findById(id)` | Tìm đơn theo mã |
| `findByIdAndPhone(id, phone)` | Chỉ trả đơn nếu **đúng của số điện thoại đó**, không thì `null` |
| `payOrder(id, paymentService)` | Khách trả qua cổng thanh toán, trả về lời nhắn biên lai |
| `confirmPaid(id)` | Quản lý xác nhận đã nhận tiền tại quầy |
| `cancelOrder(id)` | Hủy đơn chưa trả và trả hàng về kho |

### `checkout`: hai bước, có chủ ý

```java
// Bước 1: kiểm tra TẤT CẢ trước, chưa đụng vào kho
for (OrderItem item : cart.getItems()) { ... throw nếu hết hàng hoặc hết hạn ... }

// Bước 2: mọi thứ hợp lệ rồi mới trừ kho
for (OrderItem item : cart.getItems()) {
    p.setStock(p.getStock() - item.getQuantity());
    orderItems.add(new OrderItem(p, item.getQuantity()));
}
```

- Nếu giỏ có 3 món mà món thứ 3 hết hàng, bước 1 đã ném lỗi nên **không món nào bị trừ**. Không bị trừ nửa chừng.
- Giỏ có thể giữ lâu, nên phải **kiểm tra lại** kho và hạn dùng lúc chốt.
- `new OrderItem(...)` ở bước 2 tạo dòng mới nên **chốt giá hiện tại**.
- Mã đơn: `"ORD" + nextId++`, tăng sau mỗi đơn.

### `getUnpaidOrder`: một chốt chặn dùng chung

```java
private Order getUnpaidOrder(String id) {
    // không thấy → lỗi; không phải UNPAID → lỗi "is already PAID/CANCELLED"
}
```

`payOrder`, `confirmPaid`, `cancelOrder` đều gọi nó đầu tiên. Vì vậy:
- Không trả hai lần một đơn.
- Không trả một đơn đã hủy.
- Không hủy một đơn đã trả.

### `cancelOrder`
Cộng lại kho cho từng món rồi đặt `status = CANCELLED`.

### `payOrder`
```java
String receipt = paymentService.pay(order);      // gọi cổng thanh toán (đa hình)
order.setStatus(OrderStatus.PAID);
order.setPaidVia(paymentService.getName());
return receipt;
```
`OrderService` không biết đó là MoMo hay ngân hàng, nó chỉ biết đó là một `PaymentService`.

## 6. Thanh toán

```java
public interface PaymentService {
    String getName();
    String pay(Order order);   // trả về biên lai, KHÔNG in
}
```

`MoMoPaymentService` và `BankPaymentService` đều `implements PaymentService`, mỗi class trả về
chuỗi biên lai riêng. Thêm ZaloPay chỉ cần thêm **một class** mới, không sửa `OrderService`.

`pay` **trả chuỗi thay vì in**, đúng quy tắc "service không in": menu mới quyết định in ra màn hình.

## 7. `ReportService`

Class thường, không cần interface. Nhận `ProductService` và `OrderService` qua constructor để đọc dữ liệu.
Chỉ **tính toán và trả số**, `AdminMenu` lo hiển thị.

| Method | Cách tính |
|---|---|
| `getRevenue()` | Cộng tổng các đơn **PAID** |
| `getProfit()` | Cộng lãi (giá bán − giá vốn) các đơn **PAID** |
| `getUnpaidTotal()` | Tổng tiền các đơn **UNPAID** (tiền đang treo) |
| `getSoldQuantity(product)` | Cộng số lượng ở mọi đơn **không bị hủy** (dùng `equals` của Product) |
| `getBestSeller()` | Duyệt từng món, giữ món bán nhiều nhất. `null` nếu chưa bán gì |
| `getLowStock(threshold)` | Món có kho ≤ ngưỡng |
| `getExpiringSoon(days)` | Hàng tươi còn ≤ số ngày (bao gồm **đã quá hạn**) |

Một câu `instanceof` đáng chú ý:

```java
if (p instanceof PerishableProduct perishable && perishable.daysLeft() <= days) { ... }
```
Vừa kiểm tra "có phải hàng có hạn không", vừa tạo luôn biến `perishable` đã ép kiểu để dùng ngay.

Doanh thu chỉ tính đơn **đã trả** (tiền thật vào), không tính đơn chưa trả hay đã hủy.

## 8. Vòng đời một đơn, nhìn từ service

```
Cart ──checkout──▶ Order(UNPAID, kho đã trừ)
                      ├─ payOrder / confirmPaid ─▶ PAID  (tính vào doanh thu)
                      └─ cancelOrder ────────────▶ CANCELLED (trả kho)
```
Đơn đã `PAID` hoặc `CANCELLED` thì khóa, mọi thao tác tiếp theo bị chặn bởi `getUnpaidOrder`.

## 9. Điều cần nhớ

- **Dữ liệu chỉ nằm trong `ArrayList`** (bộ nhớ). Tắt chương trình là mất.
- Chưa có chức năng hoàn tiền cho đơn đã trả.
- `getAll()`, `getByStatus()`... trả danh sách mới, nhưng **các Order và Product bên trong vẫn là đối tượng thật**.
  Vì vậy menu có thể đọc và gọi service, nhưng việc thay đổi nghiệp vụ nên đi qua service.
