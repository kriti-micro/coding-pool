## Ideal corrected Java solution

```java
import java.util.*;

public class TollAnalyzer {

    /**
     * Counts unique vehicles for each booth.
     *
     * A valid record must contain exactly:
     * boothId,vehicleId,timestamp
     */
    public static Map<String, Integer> countUniqueVehicles(
            List<String> records) {

        Map<String, Set<String>> vehiclesByBooth = new HashMap<>();

        if (records == null) {
            return Collections.emptyMap();
        }

        for (String record : records) {
            if (record == null) {
                continue;
            }

            String[] parts = record.split(",", -1);

            // Require exactly three fields.
            if (parts.length != 3) {
                continue;
            }

            String boothId = parts[0].trim();
            String vehicleId = parts[1].trim();
            String timestamp = parts[2].trim();

            // Validate required fields.
            if (boothId.isEmpty()
                    || vehicleId.isEmpty()
                    || timestamp.isEmpty()) {
                continue;
            }

            vehiclesByBooth
                    .computeIfAbsent(boothId, key -> new HashSet<>())
                    .add(vehicleId);
        }

        Map<String, Integer> counts = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry
                : vehiclesByBooth.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }

        return counts;
    }

    /**
     * Returns the booth with the highest number of unique vehicles.
     *
     * Ties are resolved alphabetically by booth ID.
     */
    public static String busiestBooth(List<String> records) {
        Map<String, Integer> counts =
                countUniqueVehicles(records);

        String busiest = null;

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String currentBooth = entry.getKey();
            int currentCount = entry.getValue();

            if (busiest == null
                    || currentCount > counts.get(busiest)
                    || (currentCount == counts.get(busiest)
                    && currentBooth.compareTo(busiest) < 0)) {

                busiest = currentBooth;
            }
        }

        return busiest;
    }

    /**
     * Returns the top K booths ordered by:
     * 1. Unique-vehicle count descending
     * 2. Booth ID ascending for ties
     */
    public static List<String> topKBooths(
            List<String> records,
            int k) {

        if (k <= 0) {
            return Collections.emptyList();
        }

        Map<String, Integer> counts =
                countUniqueVehicles(records);

        List<String> booths =
                new ArrayList<>(counts.keySet());

        booths.sort((boothA, boothB) -> {
            int countComparison = Integer.compare(
                    counts.get(boothB),
                    counts.get(boothA)
            );

            if (countComparison != 0) {
                return countComparison;
            }

            return boothA.compareTo(boothB);
        });

        int resultSize = Math.min(k, booths.size());

        return new ArrayList<>(booths.subList(0, resultSize));
    }

    public static void main(String[] args) {
        List<String> records = Arrays.asList(
                "BOOTH-1,CAR-101,2026-09-13T10:15:00",
                "BOOTH-1,CAR-101,2026-09-13T10:20:00",
                "BOOTH-1,CAR-102,2026-09-13T10:25:00",
                "BOOTH-2,CAR-201,2026-09-13T10:30:00",
                "BOOTH-2,CAR-202,2026-09-13T10:35:00",
                "BOOTH-3,CAR-301,2026-09-13T10:40:00",
                "BOOTH-1,,2026-09-13T10:45:00",
                "INVALID_RECORD",
                "BOOTH-2,CAR-201,2026-09-13T10:50:00"
        );

        System.out.println(countUniqueVehicles(records));
        // Possible output: {BOOTH-1=2, BOOTH-2=2, BOOTH-3=1}

        System.out.println(busiestBooth(records));
        // BOOTH-1

        System.out.println(topKBooths(records, 2));
        // [BOOTH-1, BOOTH-2]

        System.out.println(topKBooths(records, 10));
        // [BOOTH-1, BOOTH-2, BOOTH-3]
    }
}
```

## Bugs fixed

1. **Duplicate vehicles were counted repeatedly**  
   The original code used an integer counter. A `Set<String>` is required for each booth.

2. **Malformed records were not fully validated**  
   The corrected code requires exactly three fields and checks for blank values.

3. **Whitespace was not removed**  
   Each field is trimmed before validation.

4. **`busiestBooth` did not handle ties deterministically**  
   The corrected version selects the alphabetically smaller booth when counts are equal.

5. **`topKBooths` could throw an exception**  
   `subList(0, k)` fails when `k` is greater than the number of booths. The corrected version uses:

```java
Math.min(k, booths.size())
```

6. **Integer subtraction was used in sorting**

```java
counts.get(b) - counts.get(a)
```

This can overflow for very large values. The safer version is:

```java
Integer.compare(counts.get(b), counts.get(a))
```

## Complexity

Let:

- `R` = number of records
- `V` = number of unique booth-vehicle combinations
- `B` = number of booths

For `countUniqueVehicles`:

- Time: **O(R)**
- Space: **O(V)**

For `busiestBooth`:

- Time: **O(R + B)**
- Space: **O(V)**

For `topKBooths`:

- Time: **O(R + B log B)**
- Space: **O(V + B)**

**Interview note:** If the interviewer asks specifically for the top `K` booths and `K` is much smaller than `B`, you can improve the ordering step by using a bounded min-heap, reducing it to approximately **O(R + B log K)**.

Yes, you can use **Java Streams**, but for a Karat real-world debugging task, the explicit loop approach is usually better because it is easier to explain, debug, extend, and test under time pressure.

## Stream-based solution

```java
import java.util.*;
import java.util.stream.Collectors;

public class TollAnalyzer {

    private static class ParsedRecord {
        private final String boothId;
        private final String vehicleId;

        ParsedRecord(String boothId, String vehicleId) {
            this.boothId = boothId;
            this.vehicleId = vehicleId;
        }

        String getBoothId() {
            return boothId;
        }

        String getVehicleId() {
            return vehicleId;
        }
    }

    private static Optional<ParsedRecord> parseRecord(String record) {
        if (record == null) {
            return Optional.empty();
        }

        String[] parts = record.split(",", -1);

        if (parts.length != 3) {
            return Optional.empty();
        }

        String boothId = parts[0].trim();
        String vehicleId = parts[1].trim();
        String timestamp = parts[2].trim();

        if (boothId.isEmpty()
                || vehicleId.isEmpty()
                || timestamp.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ParsedRecord(boothId, vehicleId));
    }

    public static Map<String, Integer> countUniqueVehicles(
            List<String> records) {

        if (records == null) {
            return Collections.emptyMap();
        }

        return records.stream()
                .map(TollAnalyzer::parseRecord)
                .flatMap(Optional::stream)
                .collect(Collectors.groupingBy(
                        ParsedRecord::getBoothId,
                        Collectors.mapping(
                                ParsedRecord::getVehicleId,
                                Collectors.toSet()
                        )
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));
    }

    public static String busiestBooth(List<String> records) {
        Map<String, Integer> counts =
                countUniqueVehicles(records);

        return counts.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue(
                                Comparator.reverseOrder()
                        ).thenComparing(Map.Entry::getKey)
                )
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static List<String> topKBooths(
            List<String> records,
            int k) {

        if (k <= 0) {
            return Collections.emptyList();
        }

        Map<String, Integer> counts =
                countUniqueVehicles(records);

        return counts.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue(
                                Comparator.reverseOrder()
                        ).thenComparing(Map.Entry::getKey)
                )
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
```

## Which approach should you use in the interview?

### Prefer the loop-based approach when:

- You are debugging existing code.
- You need to validate several conditions.
- You expect follow-up changes.
- You need to explain each step clearly.
- Time is limited.

The loop solution makes the core logic obvious:

```java
Map<String, Set<String>> vehiclesByBooth = new HashMap<>();
```

Then:

```java
vehiclesByBooth
        .computeIfAbsent(boothId, key -> new HashSet<>())
        .add(vehicleId);
```

This directly communicates that each vehicle should be counted only once per booth.

### Streams are reasonable when:

- The parsing logic is already clean.
- The interviewer specifically asks for a functional-style solution.
- The transformation is simple and does not require complicated branching.

## Important stream trade-offs

Both approaches have broadly similar complexity:

- Counting: **O(R)**, where `R` is the number of records
- Sorting booths: **O(B log B)**, where `B` is the number of booths
- Storage: **O(V)**, where `V` is the number of unique booth-vehicle pairs

However, the stream version can be harder to debug because the validation, grouping, deduplication, and conversion are chained together.

A strong interview response would be:

> “I could implement this with streams, but because this is an existing-code debugging and enhancement task, I would first use explicit loops for clarity and correctness. Once the behavior is tested, the grouping portion could be refactored into streams if the team prefers that style.”

Also avoid using `parallelStream()` here unless you have measured a real need. The operation is probably I/O- or input-size-dependent, and parallel collection introduces unnecessary complexity for an interview solution.