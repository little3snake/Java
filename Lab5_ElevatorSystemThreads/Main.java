public class Main {
    public static void main(String[] args) {
        ElevatorSystem system = new ElevatorSystem(3); // Initialize the system with 3 elevators
        system.start(); // Start the elevator system

        // Generate requests for 30 seconds
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            system.stopRequestGeneration(); // Stop generating new requests
        }

        // Wait until all requests are processed
        system.waitUntilAllRequestsProcessed();

        System.out.println("Elevator system stopped.");
    }
}
