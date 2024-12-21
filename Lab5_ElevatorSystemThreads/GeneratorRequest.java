import java.util.Random;

// Class representing an elevator request with source and destination floors
public class GeneratorRequest {
    private final int fromFloor; // The floor where the request originated
    private final int toFloor; // The floor where the requester wants to go

    public GeneratorRequest(int fromFloor, int toFloor) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    // Static method to generate a random request
    public static GeneratorRequest generateRandomRequest() {
        Random random = new Random();
        int fromFloor = random.nextInt(10) + 1; // Generate a random starting floor between 1 and 10
        int toFloor;
        do {
            toFloor = random.nextInt(10) + 1; // Generate a random target floor between 1 and 10
        } while (toFloor == fromFloor); // Ensure the target floor is different from the starting floor
        return new GeneratorRequest(fromFloor, toFloor);
    }

    @Override
    public String toString() {
        return "Request[from=" + fromFloor + ", to=" + toFloor + "]";
    }
}
