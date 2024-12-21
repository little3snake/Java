import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static java.lang.Math.abs;

// Class to manage the elevator system
public class ElevatorSystem {
    private final List<Elevator> elevators; // List of elevators in the system. Each elevator operates independently.
    private final ExecutorService requestGenerator; // Executor service responsible for generating requests in a separate thread.
    private final BlockingQueue<GeneratorRequest> requests; // Thread-safe queue to store incoming elevator requests.
    private final AtomicBoolean running; // Flag to indicate whether the system is running or should shut down.

    public ElevatorSystem(int numElevators) {
        elevators = new ArrayList<>();
        requests = new LinkedBlockingQueue<>();
        requestGenerator = Executors.newSingleThreadExecutor();
        running = new AtomicBoolean(true);

        // Initialize elevators
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1, requests));
        }
    }

    // Start the elevator system
    public void start() {
        // Start elevator threads
        for (Elevator elevator : elevators) {
            new Thread(elevator).start(); // Start a new thread for each elevator to handle its operations.
        }

        // Start request generator
        requestGenerator.submit(() -> {
            Random random = new Random(); // Random instance for simulating delays
            while (running.get()) {
                try {
                    // Generate a new random request
                    GeneratorRequest request = GeneratorRequest.generateRandomRequest();
                    requests.put(request);
                    System.out.println("-------------------------------");
                    System.out.println("New Request: " + request);
                    System.out.println("-------------------------------");

                    // Simulate random intervals between new requests (0-1 seconds)
                    // such small intervals for many people in one elevator.
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        System.out.println("Elevator system started.");
    }

    // Stop generating new requests but keep processing existing ones
    public void stopRequestGeneration() {
        running.set(false);
        requestGenerator.shutdownNow();
    }

    // Wait until all requests are processed
    public void waitUntilAllRequestsProcessed() {
        while (!requests.isEmpty() || elevators.stream().anyMatch(elevator -> !elevator.getCurrentRequests().isEmpty())) {
            try {
                Thread.sleep(500); // Check periodically if all requests are processed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Stop all elevators once requests are done
        for (Elevator elevator : elevators) {
            elevator.stop();
        }
    }
}
