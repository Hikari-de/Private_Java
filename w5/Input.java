import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Gom các hàm đọc bàn phím dùng chung cho AdminMenu và CustomerMenu.
// Cả chương trình chỉ có MỘT Scanner để tránh hai Scanner cùng đọc System.in.
public class Input {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This field must not be empty.");
        }
    }

    // Cho phép để trống (dùng cho trường tùy chọn như hương vị)
    public static String readOptionalText(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(sc.nextLine().trim(), DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use dd/MM/yyyy (e.g. 20/11/2026).");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        return readText(prompt + " (y/n): ").equalsIgnoreCase("y");
    }

    // In danh sách đánh số từ 1 rồi cho chọn. Trả về chỉ số 0-based, hoặc -1 nếu chọn sai.
    // Dùng được với mọi mảng, ví dụ Genre.values() hoặc ProductGroup.values().
    public static int readOption(String title, Object[] options) {
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        int choice = readInt("Enter your choice: ");
        if (choice < 1 || choice > options.length) {
            System.out.println("Invalid choice.");
            return -1;
        }
        return choice - 1;
    }
}
