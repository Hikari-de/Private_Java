
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static List<Student> students = new ArrayList<>();

    public static Student inputStudent() {
        System.out.print("Enter student ID: ");
        String studentId = sc.nextLine();

        System.out.print("Student Name: ");
        String name = sc.nextLine();

        System.out.print("Age: ");
        int age = Integer.parseInt(sc.nextLine());

        System.out.print("Car ID: ");
        String carId = sc.nextLine();

        System.out.print("Brand: ");
        String brand = sc.nextLine();

        System.out.print("Plate: ");
        String licensePlate = sc.nextLine();

        Car car = new Car(carId, brand, licensePlate);

        return new Student(studentId, name, age, car);
    }

    public static void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("Empty list.");
            return;
        }

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);

            System.out.println("Order " + (i + 1));
            System.out.println("Name: " + s.getName());
            System.out.println("Age: " + s.getAge());
            System.out.println("Car: " + s.getCar().getBrand());
            System.out.println("Plate: "
                    + s.getCar().getLicensePlate());
            System.out.println("--------------------");
        }
    }

    public static void addStudent() {
        System.out.println("Enter information of new student:");

        Student student = inputStudent();
        students.add(student);

        System.out.println("Add student succesfull!.");
    }

    public static void deleteStudent() {
        displayStudents();

        if (students.isEmpty()) {
            return;
        }

        System.out.print("Enter student order to delete: ");
        int index = Integer.parseInt(sc.nextLine());

        if (index >= 1 && index <= students.size()) {
            students.remove(index - 1);
            System.out.println("Removed.");
        } else {
            System.out.println("Invalid Order.");
        }
    }

    public static void updateStudent() {
        displayStudents();

        if (students.isEmpty()) {
            return;
        }

        System.out.print("Enter student order to modify: ");
        int index = Integer.parseInt(sc.nextLine());

        if (index < 1 || index > students.size()) {
            System.out.println("Invalid.");
            return;
        }

        Student s = students.get(index - 1);

        System.out.println("Enter new inform:");

        System.out.print("Student ID: ");
        String studentId = sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Age: ");
        int age = Integer.parseInt(sc.nextLine());

        System.out.print("Car ID: ");
        String carId = sc.nextLine();

        System.out.print("Brand: ");
        String brand = sc.nextLine();

        System.out.print("Plate: ");
        String licensePlate = sc.nextLine();


        System.out.print("Enter password to change student id: ");
        String password = sc.nextLine();

        s.setStudentId(studentId, password);
        s.setName(name);
        s.setAge(age);

        Car car = new Car(carId, brand, licensePlate);
        s.setCar(car);

        System.out.println("Updated.");
    }

    public static void findKhanh() {
        boolean found = false;

        for (Student s : students) {
            if (s.getName().equalsIgnoreCase("Khánh")) {
                System.out.println(s.introduce());
                found = true;
            }
        }

        if (!found) {
            System.out.println("404");
        }
    }

    public static void findLamborghini() {
        boolean found = false;

        for (Student s : students) {
            if (s.getCar().getBrand().equalsIgnoreCase("Lamborghini")) {
                System.out.println(s.introduce());
                found = true;
            }
        }

        if (!found) {
            System.out.println(
                    "Not exist Rich Student.");
        }
    }

    public static void checkStudentId() {
        displayStudents();

        if (students.isEmpty()) {
            return;
        }

        System.out.print("Enter order student: ");
        int index = Integer.parseInt(sc.nextLine());

        if (index < 1 || index > students.size()) {
            System.out.println("Invalid.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String id = students.get(index - 1).getStudentID(password);

        if (id != null) {
            System.out.println("Student ID: " + id);
        }
    }

    public static void changePassword() {
        System.out.print("Enter current password: ");
        String oldPassword = sc.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = sc.nextLine();

        Student.changePassword(oldPassword, newPassword);
    }

    public static void main(String[] args) {

        // Them 2 sinh vien co san
        Car car1 = new Car("CAR01", "Lamborghini", "30A-12345");
        Student s1 = new Student("SV01", "Khánh", 20, car1);

        Car car2 = new Car("CAR02", "Toyota", "30B-67890");
        Student s2 = new Student("SV02", "An", 19, car2);

        students.add(s1);
        students.add(s2);

        // Nhap sinh vien thu 3
        System.out.println("Enter 3th student:");
        students.add(inputStudent());

        int choice;

        do {
            System.out.println("\n========== MENU ==========");
            System.out.println("1. Add student");
            System.out.println("2. Delete student");
            System.out.println("3. Modify student");
            System.out.println("4. Introduce student have name K ");
            System.out.println("5. Find student have Lamborghini");
            System.out.println("6. Check student ID by password");
            System.out.println("7. Change Password");
            System.out.println("8. Show student list");
            System.out.println("0. Exist");
            System.out.print("Enter your choice: ");

            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    addStudent();
                    break;

                case 2:
                    deleteStudent();
                    break;

                case 3:
                    updateStudent();
                    break;

                case 4:
                    findKhanh();
                    break;

                case 5:
                    findLamborghini();
                    break;

                case 6:
                    checkStudentId();
                    break;

                case 7:
                    changePassword();
                    break;

                case 8:
                    displayStudents();
                    break;

                case 0:
                    System.out.println("Out Successfull.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);

        sc.close();
    }
}