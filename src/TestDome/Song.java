package TestDome;

public class Song {
    private String name;
    private Song nextSong;

    public Song(String name) {
        this.name = name;
    }

    public void setNextSong(Song nextSong) {
        this.nextSong = nextSong;
    }

    public Song getNextSong() {
        return nextSong;
    }

    public boolean isInRepeatingPlaylist() {
        Song slow = this;
        Song fast = this;

        while (fast != null && fast.getNextSong() != null) {
            slow = slow.getNextSong();                  // Move 1 step
            fast = fast.getNextSong().getNextSong();    // Move 2 steps

            if (slow == fast) {
                return true; // Cycle detected
            }
        }

        return false; // No cycle
    }

    public static void main(String[] args) {
        Song first = new Song("Hello");
        Song second = new Song("Eye of the tiger");

        first.setNextSong(second);
        second.setNextSong(first);  // Cycle here

        System.out.println(first.isInRepeatingPlaylist());  // true
    }
}

