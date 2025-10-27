package java8_stream;

public class EmployeeWithDept {
    private String firstName;
    private String lastName;
    private String dept;
    private String gender;

    public EmployeeWithDept(String firstName, String lastName, String dept, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dept = dept;
        this.gender = gender;
    }

    public String getDept() {
        return dept;
    }

    public String getGender() {
        return gender;
    }
}
