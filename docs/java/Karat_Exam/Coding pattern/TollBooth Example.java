// PART A: Bug Fix — LogEntry class
// The bug: direction assignment logic is incorrect.
// Fix: parse direction letter AFTER extracting it correctly from token[2]
class LogEntry {
    private final Float timestamp;
    private final String licensePlate;
    private final String boothType;
    // "ENTRY", "EXIT", "MAINROAD"
    private final int location;
    private final String direction;

    public LogEntry(String logLine) {
        String[] tokens = logLine.split(" ");
        this.timestamp = Float.valueOf(tokens[0]);
        this.licensePlate = tokens[1];
        this.boothType = tokens[3];
        this.location = Integer.parseInt( tokens[2].substring(0, tokens[2].length() - 1));
        // BUG: 'directionLetter' was used before being declared in original code
        String directionLetter = tokens[2].substring(tokens[2].length() - 1);
        this.direction = directionLetter.equals("E") ? "East" : "West";
    }

    public String getBoothType() {
        return boothType;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
}
// PART B: countJourneys() — Add this method to LogFile
// A complete journey = ENTRY → (0+ MAINROAD) → EXIT
public int countJourneys() {
    Map<String, Boolean> onHighway = new HashMap<>();
    int journeyCount = 0;
    for (LogEntry entry : entries) {
        String plate = entry.getLicensePlate();
        switch (entry.getBoothType()) {
            case "ENTRY":
                onHighway.put(plate, true);
                break;
            case "EXIT":
                if (onHighway.containsKey(plate)) {
                    journeyCount++;
                    onHighway.remove(plate);
                }
                break; // MAINROAD: no action needed
        }
    }
    return journeyCount;
}