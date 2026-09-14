package practise.karat;

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

