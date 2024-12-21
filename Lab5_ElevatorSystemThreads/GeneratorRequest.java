// File: Request.java
public class GeneratorRequest {
    private final int fromFloor;
    private final int toFloor;

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

    @Override
    public String toString() {
        return "Request[from=" + fromFloor + ", to=" + toFloor + "]";
    }
}