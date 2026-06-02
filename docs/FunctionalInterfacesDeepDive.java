package java21;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * FUNCTIONAL INTERFACES - DEEP DIVE
 * 
 * This file explains:
 * 1. What are functional interfaces?
 * 2. Why were they introduced?
 * 3. Core functional interfaces
 * 4. Lambda expressions enabled by functional interfaces
 * 5. Method references (shorthand for lambdas)
 */

public class FunctionalInterfacesDeepDive {
    
    // ============================================
    // PART 1: WHAT IS A FUNCTIONAL INTERFACE?
    // ============================================
    
    /**
     * A Functional Interface has EXACTLY ONE abstract method.
     * 
     * Why?
     * - Enables lambda expressions
     * - Java knows which method to implement
     * - Compiler can infer types
     */
    
    @FunctionalInterface
    interface SimpleFunction {
        String process(String input);  // Single abstract method
    }
    
    // ============================================
    // PART 2: WHY FUNCTIONAL INTERFACES?
    // ============================================
    
    static void part2_WhyFunctionalInterfaces() {
        System.out.println("\n========== PART 2: WHY FUNCTIONAL INTERFACES? ==========");
        
        System.out.println("\n1. WITHOUT FUNCTIONAL INTERFACES (Anonymous Classes - Java 5):");
        System.out.println("   Code:");
        System.out.println("   -----------------------------------------");
        System.out.println("   List<String> names = Arrays.asList(\"Kriti\", \"Deepak\", \"Rahul\");");
        System.out.println("   Collections.sort(names, new Comparator<String>() {");
        System.out.println("       @Override");
        System.out.println("       public int compare(String s1, String s2) {");
        System.out.println("           return s1.compareTo(s2);");
        System.out.println("       }");
        System.out.println("   });");
        System.out.println("   -----------------------------------------");
        System.out.println("   Lines: 8 | Readability: Poor | Maintainability: Hard");
        
        System.out.println("\n2. WITH FUNCTIONAL INTERFACES (Lambda - Java 8+):");
        System.out.println("   Code:");
        System.out.println("   -----------------------------------------");
        List<String> names = Arrays.asList("Kriti", "Deepak", "Rahul");
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        System.out.println("   Collections.sort(names, (s1, s2) -> s1.compareTo(s2));");
        System.out.println("   -----------------------------------------");
        System.out.println("   Lines: 1 | Readability: Excellent | Maintainability: Easy");
        
        System.out.println("\n   Result: " + names);
        System.out.println("\n   ✓ 87% less code!");
        System.out.println("   ✓ Much clearer intent!");
    }
    
    // ============================================
    // PART 3: CORE FUNCTIONAL INTERFACES
    // ============================================
    
    static void part3_CoreFunctionalInterfaces() {
        System.out.println("\n========== PART 3: CORE FUNCTIONAL INTERFACES ==========");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // ==========================================
        System.out.println("\n1️⃣  PREDICATE<T>");
        System.out.println("   Method: boolean test(T t)");
        System.out.println("   Purpose: Test a condition");
        System.out.println("   Use: Filtering");
        System.out.println("   Example:");
        // ==========================================
        
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("   Predicate<Integer> isEven = n -> n % 2 == 0;");
        System.out.println("   Result: " + numbers.stream().filter(isEven).toList());
        
        // ==========================================
        System.out.println("\n2️⃣  FUNCTION<T, R>");
        System.out.println("   Method: R apply(T t)");
        System.out.println("   Purpose: Transform T into R");
        System.out.println("   Use: Mapping");
        System.out.println("   Example:");
        // ==========================================
        
        Function<Integer, Integer> square = n -> n * n;
        System.out.println("   Function<Integer, Integer> square = n -> n * n;");
        System.out.println("   Result: " + numbers.stream().map(square).toList());
        
        // ==========================================
        System.out.println("\n3️⃣  CONSUMER<T>");
        System.out.println("   Method: void accept(T t)");
        System.out.println("   Purpose: Process without returning");
        System.out.println("   Use: Side effects (printing, logging, etc.)");
        System.out.println("   Example:");
        // ==========================================
        
        Consumer<Integer> print = n -> System.out.print("   " + n + " ");
        System.out.println("   Consumer<Integer> print = n -> System.out.print(n + \" \");");
        System.out.print("   Result: ");
        numbers.forEach(print);
        System.out.println();
        
        // ==========================================
        System.out.println("\n4️⃣  SUPPLIER<T>");
        System.out.println("   Method: T get()");
        System.out.println("   Purpose: Supply/Generate a value");
        System.out.println("   Use: Lazy initialization, factories");
        System.out.println("   Example:");
        // ==========================================
        
        Supplier<String> timestamp = () -> new java.util.Date().toString();
        System.out.println("   Supplier<String> timestamp = () -> new java.util.Date().toString();");
        System.out.println("   Result: " + timestamp.get());
        
        // ==========================================
        System.out.println("\n5️⃣  BIFUNCTION<T, U, R>");
        System.out.println("   Method: R apply(T t, U u)");
        System.out.println("   Purpose: Transform two inputs into one output");
        System.out.println("   Use: Binary operations");
        System.out.println("   Example:");
        // ==========================================
        
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("   BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;");
        System.out.println("   Result: " + add.apply(5, 3));
        
        // ==========================================
        System.out.println("\n6️⃣  BICONSUMER<T, U>");
        System.out.println("   Method: void accept(T t, U u)");
        System.out.println("   Purpose: Accept two values without returning");
        System.out.println("   Use: Processing pairs");
        System.out.println("   Example:");
        // ==========================================
        
        BiConsumer<String, Integer> printPair = (name, age) -> 
            System.out.println("   " + name + " is " + age + " years old");
        System.out.println("   BiConsumer<String, Integer> printPair = (name, age) -> ...");
        System.out.println("   Results:");
        printPair.accept("Kriti", 25);
        printPair.accept("Deepak", 28);
    }
    
    // ============================================
    // PART 4: LAMBDA EXPRESSIONS
    // ============================================
    
    static void part4_LambdaExpressions() {
        System.out.println("\n========== PART 4: LAMBDA EXPRESSIONS ==========");
        
        System.out.println("\n   SYNTAX: (parameters) -> { body }");
        
        System.out.println("\n   Examples:");
        System.out.println("   ─────────────────────────────────────────");
        
        // No parameters
        System.out.println("\n   1. No Parameters:");
        Supplier<Double> random = () -> Math.random();
        System.out.println("      () -> Math.random()");
        System.out.println("      Result: " + random.get());
        
        // Single parameter (no parens)
        System.out.println("\n   2. Single Parameter:");
        Consumer<String> greet = name -> System.out.println("      Hello, " + name);
        System.out.println("      name -> System.out.println(\"Hello, \" + name)");
        System.out.println("      Result:");
        greet.accept("Kriti");
        
        // Multiple parameters
        System.out.println("\n   3. Multiple Parameters:");
        BiFunction<Integer, Integer, String> compare = (a, b) -> a > b ? a + " > " + b : b + " >= " + a;
        System.out.println("      (a, b) -> a > b ? a + \" > \" + b : b + \" >= \" + a");
        System.out.println("      Result: " + compare.apply(10, 5));
        
        // Multi-line body
        System.out.println("\n   4. Multi-line Body:");
        Function<Integer, String> classify = n -> {
            if(n < 0) return "Negative";
            if(n == 0) return "Zero";
            return "Positive";
        };
        System.out.println("      n -> { if(n < 0) return \"Negative\"; ... }");
        System.out.println("      Result: " + classify.apply(-5));
    }
    
    // ============================================
    // PART 5: METHOD REFERENCES
    // ============================================
    
    static void part5_MethodReferences() {
        System.out.println("\n========== PART 5: METHOD REFERENCES ==========");
        
        System.out.println("\n   METHOD REFERENCE = Shorthand for lambda");
        System.out.println("   Syntax: object::method or Class::method");
        
        List<String> names = Arrays.asList("Kriti", "Deepak", "Rahul", "Shreya");
        
        // ==========================================
        System.out.println("\n   1. INSTANCE METHOD REFERENCE");
        System.out.println("   ─────────────────────────────");
        System.out.println("   Lambda:  s -> System.out.println(s)");
        System.out.println("   Reference: System.out::println");
        System.out.println("   Result:");
        names.forEach(System.out::println);
        
        // ==========================================
        System.out.println("\n   2. STATIC METHOD REFERENCE");
        System.out.println("   ─────────────────────────────");
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5);
        System.out.println("   Lambda:  (a, b) -> Integer.compare(a, b)");
        System.out.println("   Reference: Integer::compare");
        System.out.println("   Result (sorted): " + 
            numbers.stream().sorted(Integer::compare).toList());
        
        // ==========================================
        System.out.println("\n   3. CONSTRUCTOR REFERENCE");
        System.out.println("   ─────────────────────────────");
        System.out.println("   Lambda:  () -> new ArrayList<>()");
        System.out.println("   Reference: ArrayList::new");
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("   Result: Created new list of type " + newList.getClass().getSimpleName());
        
        // ==========================================
        System.out.println("\n   4. CHAIN METHOD REFERENCES");
        System.out.println("   ─────────────────────────────");
        System.out.println("   Lambda: name -> name.toUpperCase()");
        System.out.println("   Reference: String::toUpperCase");
        List<String> upperNames = names.stream()
            .map(String::toUpperCase)
            .toList();
        System.out.println("   Result: " + upperNames);
    }
    
    // ============================================
    // PART 6: REAL WORLD EXAMPLE
    // ============================================
    
    static class Employee {
        String name;
        String dept;
        double salary;
        
        Employee(String name, String dept, double salary) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }
        
        @Override
        public String toString() {
            return name + " (" + dept + ") - " + salary;
        }
    }
    
    static void part6_RealWorldExample() {
        System.out.println("\n========== PART 6: REAL WORLD EXAMPLE ==========");
        
        List<Employee> employees = Arrays.asList(
            new Employee("Kriti", "IT", 12000),
            new Employee("Deepak", "Finance", 22000),
            new Employee("Rahul", "IT", 15000),
            new Employee("Shreya", "Finance", 20000),
            new Employee("Diya", "IT", 18000)
        );
        
        System.out.println("\n1. USING PREDICATE - Filter high salary employees");
        System.out.println("   ─────────────────────────────────────────");
        Predicate<Employee> highSalary = e -> e.salary > 15000;
        System.out.println("   Predicate: e -> e.salary > 15000");
        employees.stream()
            .filter(highSalary)
            .forEach(e -> System.out.println("   ✓ " + e));
        
        System.out.println("\n2. USING FUNCTION - Extract names");
        System.out.println("   ─────────────────────────────────────────");
        Function<Employee, String> getName = e -> e.name;
        System.out.println("   Function: e -> e.name");
        System.out.println("   Names: " + employees.stream().map(getName).toList());
        
        System.out.println("\n3. USING CONSUMER - Process each employee");
        System.out.println("   ─────────────────────────────────────────");
        Consumer<Employee> printEmployee = e -> 
            System.out.println("   ➜ " + e.name + " works in " + e.dept);
        System.out.println("   Consumer: e -> System.out.println(...)");
        employees.forEach(printEmployee);
        
        System.out.println("\n4. USING BIFUNCTION - Compare salaries");
        System.out.println("   ─────────────────────────────────────────");
        BiFunction<Employee, Employee, String> compareSalaries = (e1, e2) ->
            e1.salary > e2.salary ? e1.name + " earns more" : e2.name + " earns more";
        System.out.println("   BiFunction: (e1, e2) -> compare salaries");
        System.out.println("   Result: " + compareSalaries.apply(employees.get(0), employees.get(1)));
        
        System.out.println("\n5. COMBINING MULTIPLE OPERATIONS");
        System.out.println("   ─────────────────────────────────────────");
        System.out.println("   Get names of IT employees with salary > 15000, sorted");
        List<String> result = employees.stream()
            .filter(e -> e.dept.equals("IT"))           // Predicate
            .filter(e -> e.salary > 15000)              // Predicate
            .sorted(Comparator.comparingDouble(e -> e.salary).reversed())  // Comparator (FI)
            .map(Employee::getName)                      // Function (via method ref)
            .collect(Collectors.toList());
        System.out.println("   Result: " + result);
    }
    
    // ============================================
    // PART 7: FUNCTIONAL INTERFACE ARCHITECTURE
    // ============================================
    
    static void part7_ArchitectureDiagram() {
        System.out.println("\n========== PART 7: FUNCTIONAL INTERFACE ARCHITECTURE ==========");
        
        System.out.println("""
            
            STREAM PIPELINE WITH FUNCTIONAL INTERFACES
            ═════════════════════════════════════════════════════════════════
            
            empList.stream()
                ├─ Predicate<Employee>
                │  .filter(e -> e.salary > 15000)
                │  boolean test(Employee e)
                │  
                ├─ Comparator<Employee>  (Extends BiFunction)
                │  .sorted(Comparator.comparingDouble(e -> e.salary))
                │  int compare(Employee e1, Employee e2)
                │  
                ├─ Function<Employee, String>
                │  .map(e -> e.name)
                │  String apply(Employee e)
                │  
                └─ Consumer<String>
                   .forEach(System.out::println)
                   void accept(String name)
                
            ═════════════════════════════════════════════════════════════════
            
            KEY INSIGHT:
            Each operation uses a Functional Interface!
            
            • filter()     → Predicate<T>
            • map()        → Function<T, R>
            • sorted()     → Comparator<T>
            • forEach()    → Consumer<T>
            • collect()    → Collector (complex but functional)
            
            WHY FUNCTIONAL INTERFACES?
            ──────────────────────────
            ✓ Enables lambda expressions
            ✓ Allows functional programming in Java
            ✓ Makes code concise and readable
            ✓ Enables method references
            ✓ Allows lazy evaluation
            """);
    }
    
    // ============================================
    // MAIN
    // ============================================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          FUNCTIONAL INTERFACES - COMPREHENSIVE GUIDE            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        
        part2_WhyFunctionalInterfaces();
        part3_CoreFunctionalInterfaces();
        part4_LambdaExpressions();
        part5_MethodReferences();
        part6_RealWorldExample();
        part7_ArchitectureDiagram();
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      SUMMARY                                   ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Functional Interface = Interface with 1 abstract method       ║");
        System.out.println("║ Purpose: Enable lambda expressions and functional programming ║");
        System.out.println("║ Core Types: Predicate, Function, Consumer, Supplier, etc.    ║");
        System.out.println("║ Benefits: Readable, concise, composable, parallelizable       ║");
        System.out.println("║ Usage: Stream operations, Callbacks, Comparators, etc.        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}

