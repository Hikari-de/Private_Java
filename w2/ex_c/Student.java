

public class Student{
    private String studentID;
    private String name;
    private int age;
    private Car car;

    private static String password = "I love HIT!";
    public Student (String studentID, String name, int age, Car car){
        this.studentID = studentID;
        this.name = name;
        this.age = age;
        this.car = car;
    }

    public String getStudentID(String inputpass) {
        if(password.equals(inputpass)) {
            return studentID;
        }
        else {
            System.out.println("Invalid pass.");
            return null;
        }
    }
    public void setStudentId(String studentID, String inputPassword) {
        if (password.equals(inputPassword)) {
            this.studentID = studentID;
        } else {
            System.out.println("Invalid pass.");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String introduce() {
        return "My name is: " + name + ", " + age
                + " years old, " + car.getBrand()
                + ", CarPlate " + car.getLicensePlate() + ".";
    }

    public static void changePassword(String oldPassword, String newPassword) {
        if (password.equals(oldPassword)) {
            password = newPassword;
            System.out.println("Change Successful!.");
        } else {
            System.out.println("Wrongpassword.");
        }
    }
}