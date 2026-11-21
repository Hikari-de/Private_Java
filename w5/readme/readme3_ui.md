# README 3: `HitShop`, `AdminMenu`, `CustomerMenu`, `Input`

Bốn file này nằm ở **thư mục gốc** của w5 và là phần **giao diện console**: in menu, đọc bàn phím,
gọi `service` để làm việc thật. Chúng không chứa luật cửa hàng.

## 1. Bốn file làm gì và gọi nhau thế nào

```
HitShop  ── tạo 1 bộ service ──┬─▶ AdminMenu    ─┐
 (main)                        └─▶ CustomerMenu ─┤── dùng Input để đọc bàn phím
                                                  └── gọi service/ để xử lý nghiệp vụ
```

| File | Vai trò |
|---|---|
| **HitShop.java** | Điểm vào chương trình. Tạo service, nạp dữ liệu mẫu, hiện menu chọn vai trò |
| **AdminMenu.java** | Giao diện chủ shop: 8 chức năng quản lý |
| **CustomerMenu.java** | Giao diện người mua: 6 chức năng |
| **Input.java** | Các hàm đọc bàn phím dùng chung |

## 2. Kiến thức Java được dùng

| Kiến thức | Dùng ở đâu |
|---|---|
| `main`, `do ... while`, `switch` | Mọi menu |
| `static` (field và method) | `Input` |
| Truyền phụ thuộc qua constructor | `AdminMenu`, `CustomerMenu` nhận service từ `HitShop` |
| `try ... catch` | Bắt `IllegalArgumentException` từ service, bắt `NumberFormatException` khi đọc số |
| `switch` trên `enum` | `AdminMenu.addProduct` (`case BOOK:`...) |
| Mảng `Object[]` và ép kiểu `(OrderStatus)` | `readOption`, `viewOrders` |
| `instanceof` kiểu mới | `viewInventory`, `checkExpiry` |
| `String.format("%,.0f", x)` | In tiền dạng `390,000` |
| `regex` đơn giản `matches("\\d{9,11}")` | Kiểm tra số điện thoại |
| `import` từ package | `model.*`, `service.*` |

## 3. `Input.java`: đọc bàn phím dùng chung

```java
private static final Scanner sc = new Scanner(System.in);
```

- **Chỉ một `Scanner` cho cả chương trình.** Hai `Scanner` cùng đọc `System.in` sẽ cướp dữ liệu của nhau.
- Mọi method là `static`, gọi thẳng `Input.readInt("...")` mà không cần `new`.

| Method | Việc làm |
|---|---|
| `readText(prompt)` | Đọc chuỗi, **không cho để trống**, hỏi lại tới khi có chữ |
| `readOptionalText(prompt)` | Đọc chuỗi, **cho phép để trống** (dùng cho hương vị đồ uống) |
| `readInt`, `readDouble` | Đọc số; nhập chữ thì báo lỗi rồi hỏi lại (`try/catch NumberFormatException`) |
| `readDate` | Đọc ngày `dd/MM/yyyy`; sai định dạng thì hỏi lại (`DateTimeParseException`) |
| `readYesNo(prompt)` | Trả `true` nếu nhập `y` |
| `readOption(title, options)` | In danh sách đánh số từ 1, cho chọn, trả **chỉ số 0-based**, sai thì trả `-1` |

`readOption` nhận `Object[]` nên dùng được với mọi mảng, ví dụ `Genre.values()`,
`ProductGroup.values()`, hoặc `{"MoMo", "HITBank"}`. Nó in từng phần tử bằng `toString`.

Mẫu dùng ở menu: nhập sai thì `-1`, menu kiểm tra rồi thoát.

```java
int g = Input.readOption("Genre", Genre.values());
if (g == -1) return;
Genre genre = Genre.values()[g];
```

## 4. `HitShop.java`: điểm vào

### `main`
```java
ProductService productService = new ProductServiceImpl();
OrderService orderService = new OrderServiceImpl();
ReportService reportService = new ReportService(productService, orderService);
seedData(productService);

AdminMenu adminMenu = new AdminMenu(productService, orderService, reportService);
CustomerMenu customerMenu = new CustomerMenu(productService, orderService);
```

Điểm quan trọng nhất của cả chương trình: **chỉ tạo MỘT bộ service rồi đưa cho cả hai menu.**
Vì vậy dữ liệu **dùng chung**: khách đặt hàng thì kho của quản lý trừ ngay, quản lý thêm hàng thì khách thấy ngay.

Kiểu khai báo `ProductService productService = new ProductServiceImpl();` (biến là **interface**,
đối tượng là **class cài đặt**) giúp sau này đổi cách lưu trữ (ví dụ ra file) mà không phải sửa menu.

### Vòng lặp menu
```
===== HITSHOP =====
1. Admin   2. Customer   0. Exit
```
Dùng `do { ... } while (choice != 0);`. Chọn 1 thì gọi `adminMenu.run()`, chọn 2 thì `customerMenu.run()`.

### `seedData`
Nạp 8 món mẫu để chạy thử ngay. Ngày hạn dùng tính theo `LocalDate.now().plusDays(...)` nên luôn tươi.
"Old Tiramisu" **cố ý hết hạn từ hôm qua** để thấy khách không thấy và không mua được.
Muốn kho trống lúc khởi động thì xóa dòng `seedData(productService);`.

### Dòng đầu `main`
```java
System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
```
Ép console in tiếng Việt và ký tự đặc biệt bằng UTF-8.

## 5. `AdminMenu.java`: giao diện quản lý

### Mật khẩu
```java
private static final String PASSWORD = "admin123";
```
`login()` cho thử tối đa 3 lần. Nhập sai cả 3 thì `run()` thoát ngay, không vào được menu.
Đây chỉ là bảo vệ cơ bản cho bài học (mật khẩu nằm thẳng trong code).

### Hằng số cảnh báo
`LOW_STOCK = 5` (kho ≤ 5 thì cảnh báo), `EXPIRY_WARN_DAYS = 3` (còn ≤ 3 ngày thì cảnh báo).

### 8 chức năng

| # | Chức năng | Cách hoạt động |
|---|---|---|
| 1 | **Add product** | Chọn nhóm → nhập mã (chặn trùng) → tên (sách thì hỏi "title") → giá bán, giá vốn, kho → thông tin riêng từng nhóm. Dùng `switch (group)` trên enum để gọi `createBook/createCake/createDrink/createFlower` |
| 2 | **View inventory** | Liệt kê mọi hàng kèm **giá vốn** và cờ `[LOW STOCK]`, `[EXPIRED]`, `[EXPIRING SOON]` |
| 3 | **Update / remove** | Chọn món, rồi: đổi giá, nhập thêm kho, hoặc xóa |
| 4 | **View orders** | Lọc theo trạng thái: tất cả, UNPAID, PAID, CANCELLED |
| 5 | **Confirm payment** | In đơn chưa trả, nhập mã, gọi `orderService.confirmPaid` |
| 6 | **Cancel order** | In đơn chưa trả, nhập mã, gọi `orderService.cancelOrder` (trả kho) |
| 7 | **Check expiry** | Kiểm tra một món, hoặc quét mọi hàng tươi sắp hết hạn hay đã hết hạn |
| 8 | **Reports** | Doanh thu, lãi, tiền chưa trả, hàng bán chạy, số bán từng món, hàng sắp hết, hàng sắp hết hạn |

### Một vài kỹ thuật đáng nhớ
- **Mảng có "All" đứng đầu** trong `viewOrders`:
  ```java
  Object[] options = {"All", OrderStatus.UNPAID, OrderStatus.PAID, OrderStatus.CANCELLED};
  ```
  Chỉ số 0 là "tất cả", các chỉ số sau ép kiểu `(OrderStatus) options[c]` để lọc.
- **Chặn nhập vô lý ngay tại menu:** giá > 0, giá vốn ≥ 0, kho ≥ 0. Luật nghiệp vụ sâu hơn (trùng mã...) do service kiểm tra.
- **`try/catch` quanh mọi lời gọi service** để in lời nhắn lỗi thay vì làm sập chương trình.
- **Chỉ ở đây giá vốn được in ra**, vì `Product.toString` không có giá vốn.

## 6. `CustomerMenu.java`: giao diện người mua

### Giỏ hàng riêng
```java
private final Cart cart = new Cart();
```
`run()` đầu tiên gọi `cart.clear()` để người mua mới có giỏ mới.

### 6 chức năng

| # | Chức năng | Cách hoạt động |
|---|---|---|
| 1 | **Browse** | Chọn nhóm (hoặc All). Chỉ hiện hàng từ `getAvailable()`, tức **còn kho và còn hạn** |
| 2 | **Add to cart** | Nhập mã và số lượng. `cart.add` kiểm tra và ném lỗi nếu hết hàng, hết hạn, vượt kho |
| 3 | **View cart** | In từng dòng và tổng tiền |
| 4 | **Edit cart** | Đổi số lượng hoặc bỏ một món |
| 5 | **Checkout** | Nhập tên, SĐT (kiểm tra 9 đến 11 chữ số) → `orderService.checkout` → hỏi "Pay now?" |
| 6 | **My orders** | Nhập SĐT → xem các đơn của mình → trả tiền hoặc hủy |

### Mảng có "All"
Trong `browseProducts`, "All" được xếp đầu `options`, nên chỉ số 0 là mọi nhóm (`group = null`)
và chỉ số `c` ứng với `groups[c - 1]`.

### Khách không đụng được đơn người khác
Ở "My orders", khi khách nhập mã đơn, menu gọi:
```java
Order order = orderService.findByIdAndPhone(orderId, phone);
```
Nếu mã đơn không thuộc về số điện thoại đó, hàm trả `null` và khách chỉ thấy "Order not found."

### Thanh toán
`payOrder` cho chọn MoMo hoặc HITBank, tạo `MoMoPaymentService` hoặc `BankPaymentService`,
rồi gọi `orderService.payOrder(id, paymentService)`. Menu chỉ chọn cổng, service lo phần còn lại.

### Khách không làm được
Tạo hay sửa sản phẩm, thấy giá vốn hay lãi, thấy đơn của người khác, mua hàng hết hạn hay hết kho.

## 7. Một kịch bản chạy hết vòng

1. `HitShop` khởi động, nạp 8 món mẫu, hiện menu chọn vai trò.
2. Chọn **2 (Customer)**, Browse nhóm CAKE: thấy "Strawberry Cupcake", **không** thấy "Old Tiramisu".
3. Add to cart: `C01` × 3. Checkout: nhập tên và SĐT → đơn `ORD1` (UNPAID), kho Cupcake còn 17.
4. Trả tiền bằng MoMo → đơn thành PAID.
5. Quay lại, chọn **1 (Admin)**, nhập `admin123`. Reports: doanh thu và lãi đã tăng, "Best seller" là Cupcake.

## 8. Cách biên dịch và chạy

Đứng trong thư mục `w5`:
```
javac -d out HitShop.java AdminMenu.java CustomerMenu.java Input.java model/*.java service/*.java
java -cp out HitShop
```
Ba file menu và `Input` nằm ở **default package** (không có dòng `package`) nên có thể `import model.*` và `import service.*`.
Ngược lại, code trong `model/` và `service/` **không** gọi được `Input` hay các menu, và đó đúng là
điều ta muốn: tầng dữ liệu và luật không phụ thuộc vào giao diện.

## 9. Giới hạn hiện tại

- Dữ liệu chỉ nằm trong bộ nhớ, tắt chương trình là mất.
- Mật khẩu admin viết thẳng trong code.
- Chưa có hoàn tiền cho đơn đã trả.
- Chưa thử tay một số nhánh: sửa giỏ, sửa giá, nhập thêm kho, xóa sản phẩm.
