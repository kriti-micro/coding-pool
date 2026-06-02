package java21;

// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java8_stream.EmployeeWithDept;

import java.util.*;
import java.util.stream.*;

    record Employee(String name,String dept,double salary){}
    class Main {
        public static void main(String[] args) {
            // INTERMEDIATE vs TERMINAL OPERATIONS - COMPLETE GUIDE
            // 
            // ╔══════════════════════════════════════════════════════════════════════════════╗
            // ║                           STREAM OPERATIONS CLASSIFICATION                  ║
            // ╠══════════════════════════════════════════════════════════════════════════════╣
            // ║                                                                              ║
            // ║  INTERMEDIATE OPERATIONS (return Stream, lazy, can chain):                 ║
            // ║  ──────────────────────────────────────────────────────────────────────     ║
            // ║  • filter()     → Stream (e.g., .filter(e -> e.salary > 15000))           ║
            // ║  • map()        → Stream (e.g., .map(Employee::getName))                  ║
            // ║  • sorted()     → Stream (e.g., .sorted(Comparator.comparingInt()))       ║
            // ║  • distinct()   → Stream (e.g., .distinct())                              ║
            // ║  • limit()      → Stream (e.g., .limit(5))                                ║
            // ║  • flatMap()    → Stream (e.g., .flatMap(List::stream))                   ║
            // ║  • mapToObj()   → Stream (e.g., .mapToObj(c -> (char)c))                  ║
            // ║  • chars()      → IntStream (e.g., "hello".chars())                       ║
            // ║  • range()       → IntStream (e.g., IntStream.range(0, 5))                  ║
            // ║  • rangeClosed()→ IntStream (e.g., IntStream.rangeClosed(0, 5))          ║
            // ║                                                                              ║
            // ║  TERMINAL OPERATIONS (return result, eager, trigger execution):           ║
            // ║  ──────────────────────────────────────────────────────────────────────     ║
            // ║  • collect()    → Collection (e.g., .collect(Collectors.toList()))        ║
            // ║  • forEach()    → void (e.g., .forEach(System.out::println))              ║
            // ║  • max()        → Optional (e.g., .max(Comparator.comparing()))           ║
            // ║  • min()        → Optional (e.g., .min(Comparator.comparing()))           ║
            // ║  • count()      → long (e.g., .count())                                   ║
            // ║  • anyMatch()   → boolean (e.g., .anyMatch(x -> x > 5))                   ║
            // ║  • allMatch()   → boolean (e.g., .allMatch(x -> x > 5))                   ║
            // ║  • noneMatch()  → boolean (e.g., .noneMatch(x -> x > 5))                  ║
            // ║  • findFirst()  → Optional (e.g., .findFirst())                           ║
            // ║  • findAny()    → Optional (e.g., .findAny())                             ║
            // ║  • reduce()     → Result (e.g., .reduce(0, Integer::sum))                ║
            // ║  • toList()     → List (e.g., .toList())                                  ║
            // ║                                                                              ║
            // ║  KEY DIFFERENCES:                                                           ║
            // ║  ─────────────────                                                           ║
            // ║  • Intermediate: Lazy, chainable, return Stream                           ║
            // ║  • Terminal: Eager, final, return non-Stream result                       ║
            // ║  • Only ONE terminal operation per stream pipeline                        ║
            // ║  • Pipeline executes only when terminal operation is called              ║
            // ║                                                                              ║
            // ╚══════════════════════════════════════════════════════════════════════════════╝
            List<Employee> empList=Arrays.asList(new Employee("Kriti","IT",12000),
                    new Employee("Kriti","IT",12000),
                    new Employee("aryan","IT",10000),
                    new Employee("Deepak","Finance",22000),
                    new Employee("Rahul","Finance",12000),
                    new Employee("Diya","IT",4000),
                    new Employee("Shreya","Finance",14000));
            List<Employee> empList1=Arrays.asList(
                    new Employee("Karthik","IT",12000),
                    new Employee("Shreya","IT",19000)
            );
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

            //List of Emp in each dept
            // Time Complexity: O(n) - iterate through all employees once
            // Space Complexity: O(n) - store all employees in the map
            Map<String,List<Employee>> empMap = empList.stream().collect(Collectors.groupingBy(Employee::dept));
            System.out.println(empMap);

            //List of emp in desc order (salary) --comparison is done before collecting in sorted() method
            // Time Complexity: O(n log n) - sorting operation
            // Space Complexity: O(n) - storing all employees in the sorted list
            List<Employee> empDescList = empList.stream().sorted(Comparator.comparingDouble(Employee::salary).reversed()).collect(Collectors.toList());
            System.out.println(empDescList);


            //[Top 2 per dept,soted DESC] VV IMP - use of collectingAndThen to do post processing after groupingBy
            //groupingBy(key, collectingAndThen(toList(), post-processing))
            // Time Complexity: O(n log n) - sorting within each department group
            // Space Complexity: O(n) - store all employees across all departments
            Map<String,List<Employee>> empDeptMap = empList.stream().distinct().collect(Collectors.groupingBy
                    (Employee::dept,
                            Collectors.collectingAndThen
                                    (Collectors.toList(),
                                            list->list
                                                    .stream()
                                                    .sorted(Comparator.comparingDouble(Employee::salary)
                                                            .reversed())
                                                    .limit(2)
                                                    .toList()
                                    )
                    )
            );
            System.out.println(empDeptMap);

            //grouping by dept n then grouping By salary range
            // Time Complexity: O(n) - iterate through all employees once for grouping
            // Space Complexity: O(n) - store all employees in the nested map groups
            Map<String,Map<String,List<Employee>>> doubleMap=empList.stream()
                    .collect(Collectors
                            .groupingBy(Employee::dept,
                                    Collectors.groupingBy(e->{
                                        if (e.salary()<10000) return "LOW";
                                        else if (e.salary()<20000) return "MEDIUM";
                                        else return "HIGH";}
                                    )
                            ));
            System.out.println(doubleMap);

            //Same as above grouping by dept n then grouping by gender n count
            // Group by Dept -> Gender -> Count
            Map<String, Map<String,Long>> map=employeeWithDeptList.stream().collect(
                    Collectors.groupingBy(EmployeeWithDept::getDept,
                            Collectors.groupingBy(EmployeeWithDept::getGender,Collectors.counting())));

            System.out.println(map);
            // Print Result
            map.forEach((dept,genderMap)->{
                System.out.println("Dept : "+dept);
                genderMap.forEach((gender,count)->System.out.println(gender+" : "+count));
            });


            //Same as above grouping by dept n then grouping by Salary Range n count
            Map<String,Map<String,Long>> doubleMap1=empList.stream().collect(
                    Collectors.groupingBy(Employee::dept,Collectors.groupingBy(e->{
                        if(e.salary()<10000) return "LOW";
                        else if(e.salary()<20000) return "MEDIUM";
                        else return "HIGH";
                    },Collectors.counting())));
            System.out.println(doubleMap1);

            //Find duplicate employees by name using Set
            // Time Complexity: O(n) - iterate through all employees once, HashSet add() is O(1)
            // Space Complexity: O(n) - store unique names in the HashSet
            HashSet<String> seen =new HashSet<>();
            List<Employee> duplicateEmpList=empList
                    .stream()
                    .filter(e->!seen.add(e.name()))
                    .toList();
            System.out.println(duplicateEmpList);
            System.out.println(seen);

            //Count employees per department
            // Time Complexity: O(n) - iterate through all employees once
            // Space Complexity: O(d) - where d is number of unique departments (d <= n)
            Map<String,Long> empDeptCount = empList
                    .stream()
                    .collect(Collectors.groupingBy(Employee::dept,Collectors.counting()));
            System.out.println(empDeptCount);

            //Highest Salary
            // Time Complexity: O(n) - iterate through all employees to find max
            // Space Complexity: O(1) - only store one Optional reference
            Optional<Employee> highestSal= empList
                    .stream()
                    .max(Comparator.comparingDouble(Employee::salary));
            System.out.println(highestSal.get());

            //group employees by salary range
            // Time Complexity: O(n) - iterate through all employees once for grouping
            // Space Complexity: O(n) - store all employees in the map groups
            Map<String,List<Employee>> salGroupMap = empList.stream()
                    .collect(Collectors.groupingBy(e->{
                        if(e.salary()<10000) return "LOW";
                        else if(e.salary()>=10000 && e.salary()<=20000) return "MEDIUM";
                        else return "HIGH";
                    }));
            System.out.println(salGroupMap);

            //Find dept by  total salary
            // Time Complexity: O(n) - iterate through all employees once for summing
            // Space Complexity: O(d) - where d is number of unique departments
            Map<String,Double> salSumGroupMap = empList.stream()
                    .collect(Collectors.groupingBy(Employee::dept,
                            Collectors.summingDouble(Employee::salary)));
            System.out.println(salSumGroupMap);

            //Find dept with highest total salary
            // Time Complexity: O(n) for grouping + O(d log d) for sorting departments - O(n) overall
            // Space Complexity: O(d) - where d is number of unique departments
            Optional<Map.Entry<String,Double>>   dept   =    empList.stream()
                    .collect(Collectors.groupingBy(Employee::dept,
                            Collectors.summingDouble(Employee::salary)))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            System.out.println(dept.get());

            //"flatMap is used to flatten nested structures. Here each List<Employee> is converted into a stream and merged into a single stream using List::stream."
            /**     | Method  | Output            |
                    | ------- | ----------------- |
                    | map     | Stream<Stream<T>> |
                    | flatMap | Stream<T>         | */
            //flattened List -IMP use of flatMap n why List::stream
            // Time Complexity: O(m*n) - where m is number of lists and n is average size of each list
            // Space Complexity: O(m*n) - storing all employees from all lists
            List<List<Employee>> multipleList = Arrays.asList(empList,empList1);
            List<Employee> flattenEmpList = multipleList.stream()
                    .flatMap(List::stream)
                    .toList();
            System.out.println(flattenEmpList);


            /**
                | Feature  | partitioningBy   | groupingBy              |
                | -------- | ---------------- | ----------------------- |
                | Groups   | Only 2           | Multiple                |
                | Key type | Boolean          | Any (String, int, etc.) |
                | Use case | Binary condition | General grouping        |  */
            //Partition By (High vs low) ? Why Boolean
            // Time Complexity: O(n) - iterate through all employees once
            // Space Complexity: O(n) - store all employees in two groups (true/false)
            Map<Boolean,List<Employee>> partitionMap=empList
                    .stream()
                    .collect(Collectors
                            .partitioningBy(e->e.salary()>15000));
            System.out.println(partitionMap)   ;

            //Find freq of each char
            //TimeComplexity O(n)
            // Time Complexity: O(n) - where n is string length, grouping each character
            // Space Complexity: O(k) - where k is number of unique characters (k <= 26 for lowercase)
            String str="swiss";
            LinkedHashMap<Character,Long> charMap=str
                    .toLowerCase()
                    .chars()
                    .mapToObj(c->(char)c)
                    .collect(Collectors
                            .groupingBy(c->c,LinkedHashMap::new,Collectors.counting()));
            System.out.println(charMap);

            //Find first non-repeating character (String)
            //TimeComplexity O(n)
            // Time Complexity: O(k) - where k is number of unique characters in the map
            // Space Complexity: O(k) - storing the character frequency map
            Character c=charMap
                    .entrySet()
                    .stream()
                    .filter(e->e.getValue()==1)
                    .findFirst()
                    .get()
                    .getKey();
            System.out.println(c);

            //Convert List To Map (handle duplicate Keys) name & no of emp
            // Time Complexity: O(n) - iterate through all employees to create map
            // Space Complexity: O(n) - store all unique employees in the map
            Map<String,Employee> noDuplicateEmpMap = empList
                    .stream()
                    .collect(Collectors.toMap(
                    e->e.name(),
                            e->e,
                            (e1,e2)->e1 //resolve duplicate
            ));
            System.out.println(noDuplicateEmpMap);

            //Reverse a string using reduce IMP - reduce
            // Time Complexity: O(n) - where n is string length, each character processed once
            // Space Complexity: O(n) - for storing the reversed string
            String reversedStr = str
                    .chars()
                    .mapToObj(s->String.valueOf((char)s))
                    .reduce("",(a,b)->b+a);
            System.out.println(reversedStr);

            //Count vowels in a string IMP - char handling
            // Time Complexity: O(n) - where n is string length
            // Space Complexity: O(1) - only storing count value
            Long count = str
                    .chars()
                    .mapToObj(ch->(char)ch)
                    .filter(ch->"aciou".indexOf(ch)!=-1)
                    .count();
            System.out.println(count);

            //Find duplicate chars set
            // Time Complexity: O(n) - where n is string length, HashSet add() is O(1)
            // Space Complexity: O(k) - where k is number of duplicate characters (k <= n)
            Set<Character> charSeen = new HashSet<>();
            Set<Character> duplicatesChar = str
                    .chars()
                    .mapToObj(dc->(char)dc)
                    .filter(dc->!charSeen.add(dc))
                    .collect(Collectors.toSet());
            System.out.println(duplicatesChar);

            //To check if 2 str anagram IMP - sorting n arrays equals
            // Time Complexity: O(n log n) - where n is string length, due to sorting
            // Space Complexity: O(n) - for storing sorted char arrays
            String s1="listen";
            String s2="silent";
            Boolean isAnagram = Arrays.equals(s1.chars().sorted().toArray(),
                    s2.chars().sorted().toArray());
            System.out.println(isAnagram);

            //Find First Repeating Character in a String using HashMap
            // Time Complexity: O(n) - where n is string length, grouping and finding first
            // Space Complexity: O(k) - where k is number of unique characters in the string
            String s3 = "Balloon";
            
            // Time Complexity: O(n) for this approach with grouping
            // Space Complexity: O(k) - LinkedHashMap stores unique characters
            Character firstRepeatingChar = s3.toLowerCase()
                    .chars()
                    .mapToObj(chf->(char)chf)
                    .collect(Collectors.groupingBy(chf->chf,LinkedHashMap::new,Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(e->e.getValue()>1)
                    .findFirst()
                    .get()
                    .getKey();

            System.out.println(firstRepeatingChar);
            
            //Find First Repeating Character in a String using HashSet
            // Time Complexity: O(n) - where n is string length, single pass with HashSet
            // Space Complexity: O(k) - where k is number of unique characters
            HashSet<Character> hcseen=new HashSet<>();
            Character firstRepeatingChar1 = s3
                    .toLowerCase().chars().mapToObj(c1->(char)c1)
                    .filter(c1->!hcseen.add(c1))
                    .findFirst()
                    .get();
            System.out.println(firstRepeatingChar1);

            //Find longest word in a sentence
            // Time Complexity: O(n) - where n is number of words in the sentence
            // Space Complexity: O(n) - for storing the words in the stream
            String s4="The longest word in the sentence is mississipi.";
            // REGEX EXPLANATION: replaceAll("[^a-zA-Z ]", "")
            // 
            // Regex Pattern: [^a-zA-Z ]
            // - [^...] = Negated character class (match anything NOT in the brackets)
            // - a-z = lowercase letters
            // - A-Z = uppercase letters  
            // - [space] = space character
            // 
            // So [^a-zA-Z ] means: "Match any character that is NOT a letter AND NOT a space"
            // This matches: punctuation (!@#$%^&*()), numbers (0123456789), special chars (.,;:'" etc.)
            // 
            // replaceAll() replaces ALL matches with empty string ""
            // 
            // Example: "Hello, world! 123" → "Hello world " (removes comma, exclamation, numbers)
            // 
            // WHY? Clean the sentence before splitting to avoid issues with punctuation
            // Without cleaning: "Hello, world!".split("\\s+") → ["Hello,", "world!"]
            // With cleaning: "Hello world".split("\\s+") → ["Hello", "world"]
            // 
            // Then split("\\s+") splits on one or more whitespace characters
            // \\s+ means: one or more whitespace (spaces, tabs, newlines)
            String cleaned = s4.replaceAll("[^a-zA-Z ]", "");
            String longestWord = Arrays.stream(cleaned.split("\\s+")).max(Comparator.comparingInt(s5->s5.length())).get(); //instead of get() use orElse("") to avoid NoSuchElementException if sentence is empty - V IMP
            System.out.println(longestWord + " " + longestWord.length());

            //Remove duplicates from a string using distinct() and Collectors.joining()
            // Time Complexity: O(n) - where n is string length, single pass with LinkedHashSet
            // Space Complexity: O(k) - where k is number of unique characters
            String s5 = "banana";
            // DIFFERENCE: distinct() BEFORE mapToObj() vs AFTER
            // BEFORE: s5.chars().distinct().mapToObj(c3->String.valueOf((char)c3)).distinct().collect(Collectors.joining());
            // AFTER:  s5.chars().mapToObj(c3->String.valueOf((char)c3)).distinct().collect(Collectors.joining());
            // 
            // distinct() BEFORE mapToObj():
            // - Works on IntStream (primitive int values)
            // - Removes duplicate ASCII values (e.g., 'a' = 97, 'n' = 110)
            // - More memory efficient (no String objects created yet)
            // - Same result for character uniqueness
            //
            // distinct() AFTER mapToObj():
            // - Works on Stream<String> (String objects)
            // - Removes duplicate String objects
            // - Uses more memory (String objects created)
            // - More flexible for complex objects
            String noDuplicateStr = s5.chars().distinct().mapToObj(c3->String.valueOf((char)c3)).collect(Collectors.joining());
            System.out.println(noDuplicateStr);

            //Sort the characters of a string in alphabetical order
            // Time Complexity: O(n log n) - where n is string length, due to sorting
            // Space Complexity: O(n) - for storing sorted characters
            String sortedStr = s5.chars().sorted().mapToObj(c4->String.valueOf((char)c4)).collect(Collectors.joining());
            System.out.println(sortedStr);

            //CheckPalindrome IMP - reverse string and compare
            // Time Complexity: O(n) - where n is string length, single pass to check
            // Space Complexity: O(1) - only storing indices and characters for comparison
            String s6 = "madam";
            // INTERMEDIATE vs TERMINAL OPERATIONS EXPLANATION:
            // 
            // INTERMEDIATE OPERATIONS (return Stream, lazy evaluation):
            // - filter(), map(), sorted(), distinct(), limit(), flatMap(), mapToObj()
            // - Can be chained together, nothing executes until terminal operation
            // - Lazy: Only process elements as needed
            // 
            // TERMINAL OPERATIONS (return result, trigger execution):
            // - collect(), forEach(), max(), min(), count(), anyMatch(), allMatch(), findFirst()
            // - Eager: Force the stream pipeline to execute
            // - Only one terminal operation per stream
            // 
            // In this line: chars() → mapToObj() → reduce() → equals()
            // - chars(): Intermediate (returns IntStream)
            // - mapToObj(): Intermediate (returns Stream<String>)
            // - reduce(): TERMINAL (returns String result)
            // - equals(): Not a stream operation
            Boolean isPalindrome = s6.chars().mapToObj(c5->String.valueOf((char)c5)).reduce("",(a,b)->b+a).equals(s6);
            System.out.println(isPalindrome);

            //Advance level using IntStream,range() and allMatch() IMP - check characters from both ends towards the center
            // Time Complexity: O(n) - where n is string length, single pass to check
            // Space Complexity: O(1) - only storing indices and characters for comparison
            // DIFFERENCE: range() vs rangeClosed()
            // 
            // IntStream.range(start, end)     → start (inclusive) to end (EXCLUSIVE)
            // IntStream.rangeClosed(start, end) → start (inclusive) to end (INCLUSIVE)
            // 
            // Example for string "madam" (length = 5):
            // - length/2 = 2 (integer division)
            // - range(0, 2)     → generates: 0, 1     (checks indices 0↔4, 1↔3)
            // - rangeClosed(0, 2) → generates: 0, 1, 2 (checks indices 0↔4, 1↔3, 2↔2)
            // 
            // For palindrome checking:
            // - We only need to check first half against second half
            // - Middle character (if odd length) doesn't need self-comparison
            // - So range(0, length/2) is CORRECT (excludes middle)
            // - rangeClosed(0, length/2) would check middle unnecessarily
            // 
            // allMatch() is TERMINAL - returns true if ALL elements match the predicate
            // Short-circuits: stops at first false (efficient!)
            Boolean isPalindrome1 = IntStream.range(0,s6.length()/2).allMatch(i->s6.charAt(i)==s6.charAt(s6.length()-1-i));
            System.out.println(isPalindrome1);

            //Group words by length
            // Time Complexity: O(n) - where n is number of words, single pass for grouping
            // Space Complexity: O(n) - for storing all words in the map groups
            // 

            // Change to: .replaceAll("[^a-zA-Z ]","")
            // This keeps: all lowercase letters (a-z), all uppercase letters (A-Z), and spaces
            //
            // HOW TO DEBUG THIS:
            // 1. Add intermediate print statements
            // 2. Print the string after regex replacement to see what's actually there
            // 3. Check the regex pattern character by character
            

            Map<Integer,List<String>> mapGroupByWordLen = Arrays.stream(s4.replaceAll("[^a-zA-Z ]","").split("\\s+"))
                    .collect(Collectors.groupingBy(s->s.length()));
            System.out.println(mapGroupByWordLen);

            //convert given string to camel case and add prefix "#"
            // Time Complexity: O(n) - where n is number of words, single pass for mapping
            // Space Complexity: O(n) - for storing the transformed words in the list
            String epamString ="hello java world";
            String result = "#"+Arrays.stream(epamString.split("\\s+"))
                    .map(str1->String.valueOf(str1.charAt(0)).toUpperCase()+str1.substring(1,str1.length()))
                    .collect(Collectors.joining());
            System.out.println(result);

        }
    }

