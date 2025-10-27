package java8_stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupEmpWithDept {
    public static void main(String[] args) {
        List<EmployeeWithDept> employeeWithDeptList = Arrays.asList(
                new EmployeeWithDept("John", "Doe", "Sales", "Male"),
                new EmployeeWithDept("Jane", "Doe", "Sales", "Female"),
                new EmployeeWithDept("Mark", "Smith", "Sales", "Male"),
                new EmployeeWithDept("Sara", "Taylor", "HR", "Female"),
                new EmployeeWithDept("Tom", "Brown", "HR", "Male"),
                new EmployeeWithDept("Alex", "Johnson", "Sales", "Male"),
                new EmployeeWithDept("Emily", "Davis", "Sales", "Female"),
                new EmployeeWithDept("Chris", "Wilson", "HR", "Male"),
                new EmployeeWithDept("Anna", "Moore", "HR", "Female"),
                new EmployeeWithDept("Peter", "Clark", "Sales", "Male")
                // ... add more to match Male=10, Female=5 for Sales etc.
        );

        // Group by Dept -> Gender -> Count
        Map<String, Map<String,Long>> map=employeeWithDeptList.stream().collect(
                Collectors.groupingBy(EmployeeWithDept::getDept,
                Collectors.groupingBy(EmployeeWithDept::getGender,Collectors.counting())));

        // Print Result
        map.forEach((dept,genderMap)->{
            System.out.println("Dept : "+dept);
            genderMap.forEach((gender,count)->System.out.println(gender+" : "+count));
        });
        
    }
}
