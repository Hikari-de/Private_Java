# README 1: Thư mục `model/`

`model/` chứa **dữ liệu** của cửa hàng: hàng hóa, khách, giỏ hàng, đơn hàng. Các class ở đây
**không đọc bàn phím** và **không quyết định luật bán hàng** (việc đó của `service/`). Chúng chỉ
biết "mình là gì" và "mình giữ những thông tin gì".

## 1. Bức tranh tổng thể

```
Product (abstract)                        Customer
 ├─ GeneralProduct                        OrderItem  ← một dòng "món + số lượng"
 ├─ Book                                  Cart       ← giỏ hàng (nháp, chưa trừ kho)
 └─ PerishableProduct (abstract)          Order      ← đơn hàng (đã chốt, đã trừ kho)
     ├─ Cake
     ├─ Drink                             Enum: Genre, ProductGroup, OrderStatus
     └─ Flower
```

Quan hệ giữa các lớp: `Order` có nhiều `OrderItem`, mỗi `OrderItem` trỏ tới một `Product`.
`Cart` cũng chứa nhiều `OrderItem`. Khi khách đặt hàng, giỏ biến thành đơn.

## 2. Kiến thức Java được dùng (tra nhanh)

| Kiến thức | Dùng ở đâu | Ý nghĩa ngắn |
|---|---|---|
| Đóng gói (`private` + getter/setter) | Mọi class | Dữ liệu để `private`, bên ngoài chỉ truy cập qua method |
| Kế thừa (`extends`) | Book, Cake, Drink... | Class con dùng lại field và method của class cha |
| Class trừu tượng (`abstract class`) | Product, PerishableProduct | Class cha không tạo trực tiếp được, chỉ để các class con kế thừa |
| Method trừu tượng (`abstract`) | `isExpensive()`, `getGroup()` | Không có thân, **bắt buộc** class con cụ thể viết lại |
| Ghi đè (`@Override`) | `toString`, `isAvailable`, ... | Class con viết lại hành vi của class cha |
| Đa hình | `isAvailable()` | Gọi cùng một method nhưng mỗi loại hàng chạy khác nhau |
| `super(...)` / `super.method()` | Constructor, `toString` | Gọi sang class cha |
| `enum` | Genre, ProductGroup, OrderStatus | Danh sách giá trị cố định, gõ sai là báo lỗi lúc biên dịch |
| `LocalDate`, `ChronoUnit` | PerishableProduct | Làm việc với ngày và đếm số ngày |
| `ArrayList`, vòng lặp `for` | Cart, Order | Danh sách và duyệt danh sách |
| `throw new IllegalArgumentException` | Cart | Báo lỗi dữ liệu không hợp lệ |
| `StringBuilder`, `String.format` | Order, mọi `toString` | Ghép chuỗi và định dạng số tiền `50,000` |
| `equals` / `hashCode` | Product | So sánh hai sản phẩm theo **mã** thay vì theo địa chỉ ô nhớ |

## 3. Ba enum

```java
public enum Genre { ROMANCE, COMEDY, DETECTIVE, SCIFI, HORROR }
public enum ProductGroup { GENERAL, BOOK, CAKE, DRINK, FLOWER }
public enum OrderStatus { UNPAID, PAID, CANCELLED }
```

- **Genre:** thể loại sách. Trước đây mỗi thể loại là một class rỗng (Romance, Comedy...).
  Thể loại chỉ là **dữ liệu**, nên gom vào một enum.
- **ProductGroup:** nhóm hàng, dùng để khách lọc khi xem hàng và để mỗi sản phẩm tự khai báo mình thuộc nhóm nào.
- **OrderStatus:** vòng đời đơn: `UNPAID` (mới tạo) → `PAID` (đã trả) hoặc `CANCELLED` (đã hủy).

Dùng enum: `Genre.SCIFI`, so sánh `status == OrderStatus.PAID`, lấy mọi giá trị `Genre.values()`.

## 4. `Product.java`: lớp cha của mọi mặt hàng

```java
public abstract class Product {
    private String id;  private String name;
    private double price;       // giá bán
    private double costPrice;   // giá vốn: chỉ quản lý được xem
    private int stock;
    ...
    public abstract boolean isExpensive();
    public abstract ProductGroup getGroup();
    public boolean isAvailable() { return stock > 0; }
}
```

**Chức năng:** giữ thông tin chung của mọi hàng: mã, tên, giá bán, giá vốn, tồn kho.

**Điểm đáng chú ý:**
- `abstract class`: không viết `new Product(...)` được. Chỉ tạo qua class con.
- `isExpensive()` và `getGroup()` là **abstract**: mỗi nhóm hàng tự quyết định ngưỡng "đắt" và tự nói mình thuộc nhóm nào.
- `isAvailable()` mặc định là `stock > 0`. Hàng có hạn dùng sẽ ghi đè (xem `PerishableProduct` ở mục 5).
- `toString()` **cố ý không in giá vốn**, vì khách cũng dùng nó để xem hàng.
- `equals`/`hashCode` so sánh theo `id`. Nhờ vậy `ReportService` đếm đúng số lượng đã bán của một món dù có nhiều tham chiếu tới nó.
- `costPrice` có getter/setter, nhưng chỉ `AdminMenu` in ra giá vốn.

## 5. Các lớp hàng hóa

### `GeneralProduct`
Hàng thường, không thêm field nào. `isExpensive()`: giá > 10.000. `getGroup()`: `GENERAL`.

### `Book`
Field riêng: `author`, `genre` (enum), `publishYear`. Không lưu `name` riêng.
- Constructor nhận `title` rồi truyền lên `super(...)` làm tên sản phẩm.
- `getTitle()` chỉ là `return getName();` cho đúng nghĩa với sách.
- `isExpensive()`: giá > 20.000.

### `PerishableProduct` (abstract)
Lớp trung gian cho **hàng có hạn dùng**: gom `expiryDate` và logic liên quan về **một chỗ**,
thay vì Cake, Drink, Flower mỗi class viết lại một lần.

```java
public boolean isExpired() { return LocalDate.now().isAfter(expiryDate); }
public long daysLeft()     { return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate); }

@Override
public boolean isAvailable() { return super.isAvailable() && !isExpired(); }
```

- `isExpired()`: hôm nay có sau ngày hết hạn không.
- `daysLeft()`: số ngày còn lại, **âm nghĩa là đã quá hạn**.
- `isAvailable()` ghi đè: "còn kho **và** chưa hết hạn". Đây là ví dụ rõ nhất của **đa hình**:
  nơi khác chỉ gọi `product.isAvailable()` mà không cần biết món đó có hạn hay không.
- `toString()` thêm ngày hết hạn dạng `dd/MM/yyyy`.

### `Cake`, `Drink`, `Flower` (kế thừa `PerishableProduct`)

| Class | Field riêng | Ngưỡng "đắt" |
|---|---|---|
| Cake | brand, flavor, calories | > 100.000 |
| Drink | volumeMl, sugarFree, carbonated, flavor | > 30.000 |
| Flower | origin, colour | > 50.000 |

- Mỗi class ở constructor gọi `super(id, name, price, costPrice, stock, expiryDate)`.
- `Drink.flavor` là chuỗi rỗng `""` khi không có hương vị; `toString` chỉ in khi khác rỗng.
- Các loại cụ thể (Coke, Tea, Rose...) **không còn là class riêng**: chúng chỉ là dữ liệu (tên, hương vị...).
  Thêm Pepsi hay hoa cúc không cần viết thêm class.

## 6. `Customer.java`
Chỉ có `name` và `phone`. Không có mã hay đăng nhập: người mua nhập tên và SĐT khi đặt hàng.
SĐT cũng là "chìa khóa" để khách tra lại đơn của mình.

## 7. `OrderItem.java`: một dòng trong giỏ hoặc đơn

```java
public OrderItem(Product product, int quantity) {
    this.product = product;  this.quantity = quantity;
    this.unitPrice = product.getPrice();       // chốt giá bán lúc này
    this.unitCost  = product.getCostPrice();   // chốt giá vốn lúc này
}
```

- **Chốt giá:** lưu giá tại thời điểm tạo dòng. Sau này admin đổi giá thì đơn cũ không bị sai doanh thu.
- `getSubtotal()` = giá chốt × số lượng. `getProfit()` = (giá bán − giá vốn) × số lượng.

## 8. `Cart.java`: giỏ hàng

**Chức năng:** nơi khách bỏ món vào, đổi số lượng, bỏ món. Giỏ là **nháp**: chưa trừ kho.

| Method | Việc làm |
|---|---|
| `add(product, quantity)` | Kiểm tra số lượng > 0, hàng còn bán được, không vượt kho. Món đã có thì **cộng dồn** |
| `setQuantity(id, quantity)` | Đổi số lượng, cũng kiểm tra kho |
| `remove(id)` | Bỏ món khỏi giỏ |
| `getTotal()` | Tổng tiền các dòng |
| `clear()`, `isEmpty()` | Làm trống, kiểm tra rỗng |
| `getItems()` | Trả về **bản sao của danh sách** để bên ngoài không tự thêm hay xóa trực tiếp |

- Lỗi báo bằng `throw new IllegalArgumentException("...")`. Menu bắt lỗi và in `e.getMessage()`.
- `find(productId)` là method `private` dùng nội bộ, so sánh mã không phân biệt hoa thường.
- Lưu ý: `getItems()` copy **danh sách**, không copy **từng OrderItem** bên trong.

## 9. `Order.java`: đơn hàng

Field: `id` (do service tự sinh), `customer`, `items`, `status`, `paidVia` (trả bằng gì, `null` tới khi trả).

- Đơn mới luôn có `status = UNPAID`.
- `getTotal()` và `getProfit()` cộng dồn từ các `OrderItem`.
- `toString()` dùng `StringBuilder` để ghép đơn thành nhiều dòng: dòng đầu là thông tin, mỗi món một dòng, cuối là tổng tiền.

## 10. Vì sao tách thế này (góc nhìn kinh doanh)

- **Giá vốn nằm trong `Product`** để biết lãi, nhưng `toString` không in nên khách không thấy.
- **`PerishableProduct`** thể hiện luật "hàng tươi hết hạn thì không bán".
- **`OrderItem` chốt giá** để sổ sách đúng dù giá đổi sau này.
- **`Cart` tách khỏi `Order`** vì khách đổi ý thoải mái trong giỏ mà không phải hủy đơn hay trả kho.
