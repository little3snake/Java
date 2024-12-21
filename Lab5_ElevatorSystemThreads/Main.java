import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class ElevatorSystem {
    private final List<Elevator> elevators;
    private final ExecutorService requestGenerator;
    private final BlockingQueue<Request> requests;
    private final AtomicBoolean running;

    public ElevatorSystem(int numElevators) {
        elevators = new ArrayList<>();
        requests = new LinkedBlockingQueue<>();
        requestGenerator = Executors.newSingleThreadExecutor();
        running = new AtomicBoolean(true);

        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i + 1));
        }
    }

    public void start() {
        // Start elevator threads
        for (Elevator elevator : elevators) {
            new Thread(elevator).start();
        }

        // Start request generator
        requestGenerator.submit(() -> {
            Random random = new Random();
            while (running.get()) {
                try {
                    Thread.sleep(random.nextInt(3000)); // Generate requests every 0-3 seconds
                    int fromFloor = random.nextInt(10) + 1;
                    int toFloor;
                    do {
                        toFloor = random.nextInt(10) + 1;
                    } while (toFloor == fromFloor);

                    Request request = new Request(fromFloor, toFloor);
                    requests.put(request);
                    System.out.println("New Request: " + request);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        System.out.println("Elevator system started.");
    }

    public void stop() {
        running.set(false);
        requestGenerator.shutdownNow();
        for (Elevator elevator : elevators) {
            elevator.stop();
        }
    }

    private class Elevator implements Runnable {
        private final int id;
        private final AtomicInteger currentFloor;
        private final List<Request> currentRequests;
        private volatile boolean active;

        public Elevator(int id) {
            this.id = id;
            this.currentFloor = new AtomicInteger(1);
            this.currentRequests = new ArrayList<>();
            this.active = true;
        }

        public void stop() {
            active = false;
        }

        @Override
        public void run() {
            while (active) {
                try {
                    // Fetch new request if none are being processed
                    if (currentRequests.isEmpty() && !requests.isEmpty()) {
                        Request newRequest = requests.poll(1, TimeUnit.SECONDS);
                        if (newRequest != null) {
                            currentRequests.add(newRequest);
                        }
                    }

                    // Process current requests
                    if (!currentRequests.isEmpty()) {
                        Request target = currentRequests.get(0);
                        int targetFloor = target.getFromFloor();
                        if (currentFloor.get() != targetFloor) {
                            moveToFloor(targetFloor);
                        } else {
                            System.out.println("Elevator " + id + " picked up: " + target);
                            targetFloor = target.getToFloor();
                            moveToFloor(targetFloor);
                            System.out.println("Elevator " + id + " dropped off: " + target);
                            currentRequests.remove(0);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void moveToFloor(int targetFloor) throws InterruptedException {
            while (currentFloor.get() != targetFloor) {
                if (currentFloor.get() < targetFloor) {
                    currentFloor.incrementAndGet();
                } else {
                    currentFloor.decrementAndGet();
                }
                System.out.println("Elevator " + id + " at floor: " + currentFloor.get());
                Thread.sleep(500); // Simulate movement time
            }
        }
    }

    private static class Request {
        private final int fromFloor;
        private final int toFloor;

        public Request(int fromFloor, int toFloor) {
            this.fromFloor = fromFloor;
            this.toFloor = toFloor;
        }

        public int getFromFloor() {
            return fromFloor;
        }

        public int getToFloor() {
            return toFloor;
        }

        @Override
        public String toString() {
            return "Request[from=" + fromFloor + ", to=" + toFloor + "]";
        }
    }

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
