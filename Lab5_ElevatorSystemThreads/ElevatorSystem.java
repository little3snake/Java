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
            elevators.add(new Elevator(i + 1));
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
        while (!requests.isEmpty() || elevators.stream().anyMatch(elevator -> !elevator.currentRequests.isEmpty())) {
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

    // Class representing an individual elevator
    private class Elevator implements Runnable {
        private final int id; // Elevator ID for identification
        private final AtomicInteger currentFloor; // Current floor of the elevator
        private final List<GeneratorRequest> currentRequests; // Requests currently being processed by this elevator
        private volatile boolean active; // Flag to indicate if the elevator is active

        public Elevator(int id) {
            this.id = id;
            this.currentFloor = new AtomicInteger(1); // Elevators start at floor 1
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
                    // Check if there are no active requests for the elevator and fetch a new one from the queue
                    if (currentRequests.isEmpty() && !requests.isEmpty()) {
                        GeneratorRequest newRequest = requests.poll(1, TimeUnit.SECONDS);
                        if (newRequest != null) {
                            currentRequests.add(newRequest);

                            List<GeneratorRequest> newRequestsOnTheFloor = fetchRequestsOnTheSameFloor();
                            if (!newRequestsOnTheFloor.isEmpty()) {
                                currentRequests.addAll(newRequestsOnTheFloor);
                            }
                        }
                    }

                    // Process current requests
                    if (!currentRequests.isEmpty()) {
                        GeneratorRequest target = currentRequests.get(0);
                        currentRequests.remove(0); // we not in fromFloor now
                        int targetFloor = target.getFromFloor();
                        GeneratorRequest remote_target = target;
                        moveToFloor(target, 0); // Move to the pickup floor
                        currentRequests.add(remote_target);

                        List <GeneratorRequest> targets = collectTargets(target);
                        remote_target = findMaxRemoteTarget(targets, targetFloor);
                        //System.out.println("remote " + remote_target + "targets" + targets + "target" + target);
                        //targetFloor = remote_target.getToFloor();
                        moveToFloor(remote_target, 1); // Move to the drop-off floor
                        //System.out.println("Elevator " + id + " dropped off: " + remote_target);
                        //currentRequests.remove(remote_target); // Remove the request after completion
                        //}
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private List<GeneratorRequest> collectTargets(GeneratorRequest target) {
            List<GeneratorRequest> targets = new ArrayList<>();
            for (GeneratorRequest request : currentRequests) {
                if ((target.getFromFloor() == request.getFromFloor())) {
                    targets.add(request);
                    System.out.println("Elevator " + id + " picked up: " + request);
                }
            }
            if (!targets.isEmpty()) {
                requests.removeAll(targets); // Remove from the global request queue
            }
            return targets;
        }

        private GeneratorRequest findMaxRemoteTarget(List <GeneratorRequest> targets, int floorNow) {
            int max_remote = 0;
            GeneratorRequest destination_target = null;
            for (GeneratorRequest request : targets) {
                if (max_remote < abs(request.getToFloor() - floorNow)) {
                    destination_target = request;
                }
            }
            return destination_target;
        }

        private List<GeneratorRequest> deleteRequests (GeneratorRequest target, int to_or_from) {
            List<GeneratorRequest> delete_list = new ArrayList<>();
            for (GeneratorRequest request : currentRequests) {
                if (request.getToFloor() == currentFloor.get()) {
                    if (to_or_from == 1) {
                        System.out.println("Elevator " + id + " dropped off: " + request);
                        delete_list.add(request);
                    } else {
                        boolean up1 = false, up2 = false;
                        boolean down1 = false, down2 = false;
                        up1 = (target.getFromFloor() <= request.getFromFloor()) && (currentFloor.get() > request.getFromFloor());
                        down1 = (target.getFromFloor() >= request.getFromFloor()) && (currentFloor.get() < request.getFromFloor());
                        //up2 = (target.getToFloor() > request.getToFloor()) && (currentFloor.get() < request.getToFloor());
                        //down2 = (target.getFromFloor() < request.getFromFloor()) && (currentFloor.get() > request.getFromFloor());
                        if (up1 || down1) {
                            System.out.println("Elevator " + id + " dropped off: " + request);
                            delete_list.add(request);
                        }
                    }
                }
            }
            return delete_list;
        }

        private List<GeneratorRequest> fetchRequestsOnTheWay(GeneratorRequest target, int to_or_from) {
            List<GeneratorRequest> onTheWayRequests = new ArrayList<>();
            for (GeneratorRequest request : requests) {
                if (isOnTheWay(request, target, to_or_from)) {
                    onTheWayRequests.add(request);
                }
            }
            if (!onTheWayRequests.isEmpty()) {
                requests.removeAll(onTheWayRequests); // Remove from the global request queue
            }
            return onTheWayRequests;
        }

        private List<GeneratorRequest> fetchRequestsOnTheSameFloor() {
            List<GeneratorRequest> onTheFloorRequests = new ArrayList<>();
            for (GeneratorRequest request : requests) {
                if (isOnTheFloor(request)) {
                    onTheFloorRequests.add(request);
                }
            }
            if (!onTheFloorRequests.isEmpty()) {
                requests.removeAll(onTheFloorRequests); // Remove from the global request queue
            }
            return onTheFloorRequests;
        }

        private boolean isOnTheWay(GeneratorRequest request, GeneratorRequest target, int to_or_from) {
            // Check if the request is on the current path of the elevator
            int currentFloor = this.currentFloor.get();
            if (currentFloor == request.getFromFloor()) {
                boolean sign_target =  ((target.getFromFloor() - target.getToFloor()) >= 0);
                boolean sign_request =  ((request.getFromFloor() - request.getToFloor()) >= 0);
                boolean under = false;
                boolean above = false;
                if (target.getFromFloor() >= request.getToFloor() && request.getToFloor() > currentFloor) {
                    under = true;
                }
                if (target.getFromFloor() <= request.getToFloor() && request.getToFloor() < currentFloor) {
                    above = true;
                }
                boolean is_in_way_to = under || above;
                if (to_or_from == 0) {
                    if ((sign_target && sign_request) || (!sign_target && !sign_request) || (is_in_way_to)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if ((sign_target && sign_request) || (!sign_target && !sign_request)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        private boolean isOnTheFloor(GeneratorRequest request) {
            // Check if the request is on the current path of the elevator
            int targetFloor = currentRequests.get(0).getFromFloor();
            return (request.getFromFloor() == targetFloor);
        }

        // Simulate the elevator moving to the target floor step by step
        // to_or_from = 0 --> we move to FromFloor -- elevator is empty (in most cases)
        // to_or_from = 1 --> we move to FoFloor -- elevator is full
        private void moveToFloor(GeneratorRequest target, int to_or_from) throws InterruptedException {
            int targetFloor;
            if (to_or_from == 0) {
                targetFloor = target.getFromFloor();
            // if to_or_from = 1 --> to ToFloor
            } else {
                targetFloor = target.getToFloor();
            }

            while (currentFloor.get() != targetFloor) {

                if (currentFloor.get() < targetFloor) {
                    currentFloor.incrementAndGet();
                } else {
                    currentFloor.decrementAndGet();
                }

                System.out.println("Elevator " + id + " at floor: " + currentFloor.get() + " " + currentRequests);
                Thread.sleep(750); // Simulate movement time

                // Check if there are new requests on the way (only while we move to FromFloor
                if (!requests.isEmpty()) {
                    List<GeneratorRequest> newRequestsOnTheWay = fetchRequestsOnTheWay(target, to_or_from);
                    if (!newRequestsOnTheWay.isEmpty()) {
                        // If there are requests, add them and notify
                        currentRequests.addAll(newRequestsOnTheWay);
                        for (GeneratorRequest request : newRequestsOnTheWay) {
                            System.out.println("Elevator " + id + " picked up a passenger on the way: " + request);
                        }
                    }
                }
                List<GeneratorRequest> delete_list = deleteRequests(target, to_or_from);

                if (!delete_list.isEmpty()) {
                    //System.out.println("Delete " + delete_list + " to or from " + to_or_from);
                    currentRequests.removeAll(delete_list);
                }

            }
        }
    }
}
