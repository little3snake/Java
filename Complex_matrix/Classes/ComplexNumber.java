package Classes;

import static java.lang.Math.abs;

public class ComplexNumber {
    // complex number x + iy
    private double x;
    private double y;

    // 3 Constructors (explicit constructor invocation)
    public ComplexNumber(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public ComplexNumber() {
        this(0,0);
    }
    // for non-complex numbers
    public ComplexNumber(double x) {
        this(x, 0);
    }

    // getters and setters
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }

    // "+"
    public ComplexNumber add( ComplexNumber other) {
        return new ComplexNumber(this.x + other.x, this.y + other.y);
    }
    // "-"
    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(this.x - other.x, this.y - other.y);
    }

    // "*"
    public ComplexNumber multiply(ComplexNumber other) {
        double x_mul = this.x * other.x - this.y * other.y;
        double y_mul = this.x * other.y + this.y * other.x;
        return new ComplexNumber(x_mul, y_mul);
    }

    // "/"
    // a + bi   (ac + bd) +(bc - ad)i
    // c + di         c*c + d*d
    public ComplexNumber divide(ComplexNumber other) {
        double denominator = other.x * other.x + other.y * other.y;
        double x_div = (this.x * other.x + this.y * other.y) / denominator;
        double y_div = (this.y * other.x - this.x * other.y) / denominator;
        return new ComplexNumber(x_div, y_div);
    }

    //                                                for printMatrix
    @Override
    public String toString() {
        if (y>=0)
            return x + " + " + y + "i ";
        else
            return x + " - " + abs(y) + "i "; // for nice printing "-"
    }
}
