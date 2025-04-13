package TestDome;

public class GameSpeedCalculator {

    public static double calculateFinalSpeed(double initialSpeed, int[] terrainInclines) {
        double speed = initialSpeed;

        for (int degree : terrainInclines) {
            speed -= degree * 1.0;

            if (speed <= 0) {
                return 0.0;
            }
        }

        return speed;
    }

    public static void main(String[] args) {
        System.out.println(calculateFinalSpeed(60.0, new int[] { 0, 30, 0, -45, 0 }));  // Should print 75.0
    }
}
