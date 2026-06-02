package java21;

import java.util.*;
import java.util.stream.*;

/**
 * STREAMS vs TRADITIONAL LOOPS - PRACTICAL COMPARISON
 * 
 * This file demonstrates side-by-side comparison of:
 * 1. Traditional for loops
 * 2. Sequential streams
 * 3. Parallel streams
 * 4. Performance impact
 * 5. Functional interfaces in action
 */

public class StreamsVsLoopsComparison {
    
    static class Employee {
        String name;
        String dept;
        double salary;
        int id;
        
        Employee(String name, String dept, double salary, int id) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
            this.id = id;
        }
        
        @Override
        public String toString() {
            return name + " (" + dept + ") - " + salary;
        }
    }
    
    static List<Employee> createLargeDataset(int size) {
        List<Employee> employees = new ArrayList<>();
        String[] depts = {"IT", "Finance", "HR", "Marketing", "Sales"};
        for(int i = 0; i < size; i++) {
            employees.add(new Employee(
                "Emp" + i,
                depts[i % 5],
                10000 + Math.random() * 40000,
                i
            ));
        }
        return employees;
    }
    
    // ============================================
    // EXAMPLE 1: FILTERING EMPLOYEES
    // ============================================
    
    static void example1_Filtering() {
        System.out.println("\n========== EXAMPLE 1: FILTERING ==========");
        List<Employee> empList = createLargeDataset(1000000);
        
        // Traditional For Loop
        System.out.println("\n1. TRADITIONAL FOR LOOP:");
        long start = System.currentTimeMillis();
        List<Employee> filtered1 = new ArrayList<>();
        for(Employee emp : empList) {
            if(emp.salary > 20000) {  // Only high salary
                filtered1.add(emp);
            }
        }
        long loopTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + loopTime + "ms | Result size: " + filtered1.size());
        
        // Sequential Stream
        System.out.println("\n2. SEQUENTIAL STREAM:");
        start = System.currentTimeMillis();
        List<Employee> filtered2 = empList.stream()
            .filter(e -> e.salary > 20000)
            .collect(Collectors.toList());
        long streamTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + streamTime + "ms | Result size: " + filtered2.size());
        
        // Parallel Stream
        System.out.println("\n3. PARALLEL STREAM:");
        start = System.currentTimeMillis();
        List<Employee> filtered3 = empList.parallelStream()
            .filter(e -> e.salary > 20000)
            .collect(Collectors.toList());
        long parallelTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + parallelTime + "ms | Result size: " + filtered3.size());
        
        System.out.println("\n   ANALYSIS:");
        System.out.printf("   Loop vs Stream: %.1f%% slower%n", ((streamTime - loopTime) * 100.0 / loopTime));
        System.out.printf("   Stream vs Parallel: %.1fx faster%n", (double)streamTime / parallelTime);
    }
    
    // ============================================
    // EXAMPLE 2: FILTERING + SORTING
    // ============================================
    
    static void example2_FilteringAndSorting() {
        System.out.println("\n========== EXAMPLE 2: FILTERING + SORTING ==========");
        List<Employee> empList = createLargeDataset(100000);
        
        // Traditional For Loop
        System.out.println("\n1. TRADITIONAL FOR LOOP (Verbose):");
        long start = System.currentTimeMillis();
        List<Employee> result1 = new ArrayList<>();
        for(Employee emp : empList) {
            if(emp.salary > 15000 && emp.dept.equals("IT")) {
                result1.add(emp);
            }
        }
        Collections.sort(result1, (e1, e2) -> Double.compare(e2.salary, e1.salary));
        long loopTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + loopTime + "ms | Results: " + result1.size());
        System.out.println("   Lines of Code: ~10");
        
        // Sequential Stream
        System.out.println("\n2. SEQUENTIAL STREAM (Readable):");
        start = System.currentTimeMillis();
        List<Employee> result2 = empList.stream()
            .filter(e -> e.salary > 15000 && e.dept.equals("IT"))
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .collect(Collectors.toList());
        long streamTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + streamTime + "ms | Results: " + result2.size());
        System.out.println("   Lines of Code: 4 (More readable)");
        
        // Parallel Stream
        System.out.println("\n3. PARALLEL STREAM (Fastest):");
        start = System.currentTimeMillis();
        List<Employee> result3 = empList.parallelStream()
            .filter(e -> e.salary > 15000 && e.dept.equals("IT"))
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .collect(Collectors.toList());
        long parallelTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + parallelTime + "ms | Results: " + result3.size());
        
        System.out.println("\n   VERDICT: Stream is more readable AND potentially faster!");
    }
    
    // ============================================
    // EXAMPLE 3: FUNCTIONAL INTERFACES IN ACTION
    // ============================================
    
    static void example3_FunctionalInterfaces() {
        System.out.println("\n========== EXAMPLE 3: FUNCTIONAL INTERFACES ==========");
        List<Employee> empList = createLargeDataset(10);
        
        System.out.println("\n1. PREDICATE<T> - Filter Operation:");
        // Predicate: boolean test(T t)
        java.util.function.Predicate<Employee> highSalary = e -> e.salary > 15000;
        System.out.println("   Employees with salary > 15000:");
        empList.stream()
            .filter(highSalary)
            .forEach(e -> System.out.println("   - " + e));
        
        System.out.println("\n2. FUNCTION<T,R> - Transformation:");
        // Function: R apply(T t)
        java.util.function.Function<Employee, String> getEmployeeInfo = 
            e -> e.name + " earns " + e.salary;
        System.out.println("   Transform to strings:");
        empList.stream()
            .map(getEmployeeInfo)
            .forEach(s -> System.out.println("   - " + s));
        
        System.out.println("\n3. CONSUMER<T> - Process without returning:");
        // Consumer: void accept(T t)
        java.util.function.Consumer<Employee> printEmployee = 
            e -> System.out.println("   Employee: " + e.name + " from " + e.dept);
        System.out.println("   Process each employee:");
        empList.stream()
            .forEach(printEmployee);
        
        System.out.println("\n4. COMPARATOR<T> - Sorting:");
        System.out.println("   Sort by salary (descending):");
        empList.stream()
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .limit(3)
            .forEach(e -> System.out.println("   - " + e));
    }
    
    // ============================================
    // EXAMPLE 4: LAZY EVALUATION
    // ============================================
    
    static void example4_LazyEvaluation() {
        System.out.println("\n========== EXAMPLE 4: LAZY EVALUATION ==========");
        List<Employee> empList = createLargeDataset(100);
        
        System.out.println("\n1. WITHOUT TERMINAL OPERATION (Lazy):");
        System.out.println("   Creating stream pipeline (NOT executing)...");
        Stream<Employee> pipeline = empList.stream()
            .filter(e -> {
                System.out.println("      [Filter] Checking: " + e.name);
                return e.salary > 20000;
            })
            .limit(5);
        System.out.println("   Pipeline created. Nothing printed above!");
        
        System.out.println("\n2. WITH TERMINAL OPERATION (Executes):");
        System.out.println("   Adding forEach (terminal operation)...");
        pipeline.forEach(e -> System.out.println("   - " + e));
        System.out.println("   NOW the pipeline executed!");
        
        System.out.println("\n   WHY LAZY EVALUATION?");
        System.out.println("   - Skips unnecessary work");
        System.out.println("   - Enables short-circuit evaluation");
        System.out.println("   - .findFirst() stops at first match");
        System.out.println("   - .limit(n) stops after n items");
    }
    
    // ============================================
    // EXAMPLE 5: GROUPING (Complex operation)
    // ============================================
    
    static void example5_Grouping() {
        System.out.println("\n========== EXAMPLE 5: GROUPING BY DEPARTMENT ==========");
        List<Employee> empList = createLargeDataset(1000);
        
        System.out.println("\n1. TRADITIONAL FOR LOOP APPROACH:");
        System.out.println("   (Very verbose and error-prone)");
        long start = System.currentTimeMillis();
        Map<String, List<Employee>> groupedTraditional = new HashMap<>();
        for(Employee emp : empList) {
            String dept = emp.dept;
            if(!groupedTraditional.containsKey(dept)) {
                groupedTraditional.put(dept, new ArrayList<>());
            }
            groupedTraditional.get(dept).add(emp);
        }
        // Now sort each group...
        for(List<Employee> group : groupedTraditional.values()) {
            Collections.sort(group, (e1, e2) -> Double.compare(e2.salary, e1.salary));
        }
        long loopTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + loopTime + "ms");
        System.out.println("   Lines of code: ~15+");
        
        System.out.println("\n2. STREAM APPROACH (Elegant):");
        start = System.currentTimeMillis();
        Map<String, List<Employee>> groupedStream = empList.stream()
            .collect(Collectors.groupingBy(
                e -> e.dept,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                        .toList()
                )
            ));
        long streamTime = System.currentTimeMillis() - start;
        System.out.println("   Time: " + streamTime + "ms");
        System.out.println("   Lines of code: ~6");
        
        System.out.println("\n   RESULTS BY DEPARTMENT:");
        groupedStream.forEach((dept, emps) -> {
            System.out.println("   " + dept + ": " + emps.size() + " employees");
            System.out.println("      Top 2: " + emps.stream().limit(2).map(e -> e.name).collect(Collectors.joining(", ")));
        });
    }
    
    // ============================================
    // EXAMPLE 6: WHEN TO USE WHAT
    // ============================================
    
    static void example6_WhenToUseWhat() {
        System.out.println("\n========== EXAMPLE 6: WHEN TO USE WHAT ==========");
        
        System.out.println("\n✅ USE STREAMS WHEN:");
        System.out.println("   1. Complex transformations/pipelines");
        List<Employee> emps = createLargeDataset(10);
        int highSalaryCount = (int) emps.stream()
            .filter(e -> e.salary > 20000)
            .map(e -> e.name)
            .count();
        System.out.println("      Example: Count high salary employees - Result: " + highSalaryCount);
        
        System.out.println("\n   2. Large datasets (> 100K records)");
        System.out.println("      → Use parallelStream() for 3-5x speedup");
        
        System.out.println("\n   3. Grouping/Collecting data");
        Map<String, Long> deptCounts = emps.stream()
            .collect(Collectors.groupingBy(e -> e.dept, Collectors.counting()));
        System.out.println("      Example: Count per dept - " + deptCounts);
        
        System.out.println("\n❌ USE FOR LOOPS WHEN:");
        System.out.println("   1. Need early termination");
        System.out.println("      for(Employee e : emps) {");
        System.out.println("          if(e.id == 5) return e;  // Exit immediately");
        System.out.println("      }");
        
        System.out.println("\n   2. Very small lists (< 10 items)");
        System.out.println("      Overhead not worth it");
        
        System.out.println("\n   3. Modifying state (bad practice)");
        System.out.println("      int[] count = {0};");
        System.out.println("      emps.stream().forEach(e -> count[0]++);  // ❌ Side effect!");
    }
    
    // ============================================
    // MAIN
    // ============================================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     STREAMS vs TRADITIONAL LOOPS - COMPREHENSIVE COMPARISON     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
        example1_Filtering();
        example2_FilteringAndSorting();
        example3_FunctionalInterfaces();
        example4_LazyEvaluation();
        example5_Grouping();
        example6_WhenToUseWhat();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY TAKEAWAYS                              ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Sequential streams: ~7-15% slower than loops (acceptable)   ║");
        System.out.println("║ 2. Parallel streams: 3-5x faster on multi-core systems        ║");
        System.out.println("║ 3. Functional interfaces enable lambda expressions            ║");
        System.out.println("║ 4. Streams reduce code complexity significantly               ║");
        System.out.println("║ 5. Lazy evaluation optimizes performance                      ║");
        System.out.println("║ 6. Readability gain > Performance cost for most apps          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}

