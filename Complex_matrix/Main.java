import Classes.ComplexNumber;
import Classes.Matrix;

public class Main {
    public static void main(String[] args) {
        // These matrix is created without "y" part for nice counting det and nice dividing
        Matrix A = new Matrix(3, 3);
        A.setDataByXY(0, 0, new ComplexNumber(1, 0));
        A.setDataByXY(0, 1, new ComplexNumber(2, 0));
        A.setDataByXY(0, 2, new ComplexNumber(3, 0));
        A.setDataByXY(1, 0, new ComplexNumber(4, 0));
        A.setDataByXY(1, 1, new ComplexNumber(3, 0));
        A.setDataByXY(1, 2, new ComplexNumber(9, 0));
        A.setDataByXY(2, 0, new ComplexNumber(5, 0));
        A.setDataByXY(2, 1, new ComplexNumber(7, 0));
        A.setDataByXY(2, 2, new ComplexNumber(8, 0));

        Matrix B = new Matrix(3, 3);
        B.setDataByXY(0, 0, new ComplexNumber(2, 0));
        B.setDataByXY(0, 1, new ComplexNumber(0, 0));
        B.setDataByXY(0, 2, new ComplexNumber(1, 0));
        B.setDataByXY(1, 0, new ComplexNumber(0, 0));
        B.setDataByXY(1, 1, new ComplexNumber(-3, 0));
        B.setDataByXY(1, 2, new ComplexNumber(-1, 0));
        B.setDataByXY(2, 0, new ComplexNumber(-2, 0));
        B.setDataByXY(2, 1, new ComplexNumber(4, 0));
        B.setDataByXY(2, 2, new ComplexNumber(0, 0));

        System.out.println("Matrix A:");
        A.printMatrix();

        System.out.println("\nMatrix B:");
        B.printMatrix();

        System.out.println("\nA + B:");
        Matrix C = A.add(B);
        C.printMatrix();

        System.out.println("\nA - B:");
        Matrix D = A.subtract(B);
        D.printMatrix();

        System.out.println("\nA * B:");
        Matrix E = A.multiply(B);
        E.printMatrix();

        // it's A*(B^(-1))
        System.out.println("\nA / B:");
        Matrix F = A.divide(B);
        F.printMatrix();

        System.out.println("\nDeterminant of A:");
        ComplexNumber detA = A.determinant();
        System.out.println(detA);

        System.out.println("\nDeterminant of B:");
        ComplexNumber detB = B.determinant();
        System.out.println(detB);
    }
}
