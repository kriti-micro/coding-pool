package java8_stream;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamExercises {
    public static void main(String[] args) {


        //Order of Procesing : first terminal operation invoked i.e. find Any then map and filter
        Stream<String> nameStream = Stream.of("mohan","john","vaibhav","amit");
        Stream<String> nameStartJ = nameStream.map(
                        (s) ->
                        {
                            System.out.println("Map: "+s);
                            return s.toUpperCase();

                        })
                .filter(
                        (s) ->
                        {
                            System.out.println("Filter: "+s);
                            return s.startsWith("J");
                        }
                );

        Optional<String> findAny = nameStartJ.findAny();
        System.out.println("Final output: "+findAny.get());

        //Convert IntStream to String
        String intToString = IntStream.of(12,15,20)
                .mapToObj(s->""+s)
                .collect(Collectors.joining("-"));

        System.out.println(intToString);

        //We will do operations on below list
        List<Employee> employeesList=getListOfEmployees();
        List<Employee> employeesNaturalSort =employeesList.stream().sorted().collect(Collectors.toList());
        System.out.println(employeesNaturalSort);

        //custom Sort
        List<Employee> employeesCustomSort =employeesList.stream().sorted((e1,e2)->e1.getAge()-e2.getAge()).collect(Collectors.toList());
        System.out.println(employeesCustomSort);

        //Using method Ref
        List<Employee> employeesCustomSortMethodRef =employeesList.stream().sorted(Comparator.comparing(Employee::getAge)).collect(Collectors.toList());
        System.out.println(employeesCustomSortMethodRef);

        //Using method Ref and reverse
        List<Employee> employeesCustomSortMethodRefRev =employeesList.stream().sorted(Comparator.comparing(Employee::getAge,Comparator.reverseOrder())).collect(Collectors.toList());
        System.out.println(employeesCustomSortMethodRefRev);

        //Using flatMap
        List<String> listOfCities=employeesList.stream()
                .flatMap(e->e.getListOfCities().stream())
                .collect(Collectors.toList());
        System.out.println(listOfCities);

        //find name of Person with minimum age using reduce
        employeesList.stream()
                .reduce((e1,e2)->e1.getAge()<e2.getAge()?e1:e2)
                .ifPresent(System.out::println);

        //Sum of ages using reduce
        int sumAge =employeesList.stream()
                        .map(e->e.getAge())
                        .reduce(0,(age1,age2)->age1+age2);

        System.out.println("Sum of ages of all Employees: "+sumAge);

        //allMatch
        boolean allMatch = employeesList.stream()
                .allMatch(e ->e.getAge()>18);

        System.out.println("Are all the employess adult: " +allMatch);

        //anyMatch
        boolean anyMatch = employeesList.stream()
                .anyMatch(e ->e.getAge()>30);

        System.out.println("is any employee's age greater than 30: " +anyMatch);

        //min()
        Employee minAgeEmp = employeesList.stream()
                .min(Comparator.comparing(Employee::getAge)).get();
        System.out.println("Employee with minimum age is: " +minAgeEmp);

        //max()
        Employee maxAgeEmp = employeesList.stream()
                .max(Comparator.comparing(Employee::getAge)).get();
        System.out.println("Employee with maxium age is: " +maxAgeEmp);

        //parallel
        int[] array= {1,2,3,4,5};

        System.out.println("=================================");
        System.out.println("Using Parallel Stream");
        System.out.println("=================================");
        IntStream intParallelStream=Arrays.stream(array).parallel();
        intParallelStream.forEach((s)->
                {
                    System.out.println(s+" "+Thread.currentThread().getName());
                }
        );

        //Ex 1
        employeesList.stream().filter(e->e.getAge()>30).map(e->e.getName()).forEach(System.out::println);

        //Ex2
        long c=employeesList.stream().filter(e->e.getAge()>25).count();
        System.out.println(c);

        //Ex3
        Employee emp = employeesList.stream().filter(e->e.getName().equals("John")).findAny().get();
        System.out.println(emp);

        //Example of NameSorting which implemented by comparable in EmployeeClass
        //  Collections.sort(employeesList);

        //employeesList.sort(Comparator.naturalOrder()); //if Employee implements Comparable otherwise ClassCastException
        //System.out.println("Natural Sorting : "+employeesList);

        List<Employee> sortedNameList=employeesList.stream().sorted().toList();
        System.out.println("NameSorting : "+sortedNameList);

        //Example of Age Sorting
        List<Employee> sortedAgeList=employeesList.stream()
                .sorted(Comparator.comparing(Employee::getAge))
                .toList();
        System.out.println("Age Sorting : "+sortedAgeList);

        //Ex4 & Ex5
        Employee highPaidEmp=employeesList.stream()
                .sorted(Comparator.comparing(Employee::getAge,Comparator.reverseOrder()))
                .limit(1).findAny().get();
        System.out.println(highPaidEmp);

        //Ex6 Joining
        String empStr=employeesList.stream().map(e->e.getName()).collect(Collectors.joining(","));
        System.out.println(empStr);

        //Ex7 groupingByName
        System.out.println("groupingByName : ");
        Map<String,List<Employee>> map=employeesList.stream().collect(Collectors.groupingBy(Employee::getName));
        map.forEach((empl,employeeList)->System.out.println(empl+""+employeeList));
        //or we can display like below
        map.entrySet().stream().forEach(e->System.out.println(e.getKey()+" "+e.getValue()));

    }

    public static List<Employee> getListOfEmployees() {

        List<Employee> listOfEmployees = new ArrayList<>();

        Employee e1 = new Employee("Mohan", 24,Arrays.asList("Newyork","Banglore"));
        Employee e2 = new Employee("John", 27,Arrays.asList("Paris","London"));
        Employee e3 = new Employee("Vaibhav", 32,Arrays.asList("Pune","Seattle"));
        Employee e4 = new Employee("Amit", 22,Arrays.asList("Chennai","Hyderabad"));

        listOfEmployees.add(e1);
        listOfEmployees.add(e2);
        listOfEmployees.add(e3);
        listOfEmployees.add(e4);

        return listOfEmployees;
    }

}
