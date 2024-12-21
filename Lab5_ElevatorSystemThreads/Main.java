import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;


public class Main {
    public static void main(String[] args) {
        ElevatorSystem system = new ElevatorSystem(3);
        system.start();

        // Run system for 30 seconds
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            system.stop();
        }

        System.out.println("Elevator system stopped.");
    }
}
