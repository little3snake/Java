package Classes;

//import java.util.Objects;

public class Matrix {
    private ComplexNumber[][] data;
    private int rows;
    private int cols;

    // Constructors
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new ComplexNumber[rows][cols];

        // filling the matrix with zeros
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = new ComplexNumber();
            }
        }
    }

    //getters ans setters
    public ComplexNumber getDataByXY(int row, int col) {
        return data[row][col];
    }
    public void setDataByXY(int row, int col, ComplexNumber value) {
        data[row][col] = value;
    }
    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    public int getCols() {
        return cols;
    }
    public void setCols(int cols) {
        this.cols = cols;
    }

    // "+"
    public Matrix add(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Матрицы должны иметь одинаковый размер.");
        }

        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = this.data[i][j].add(other.data[i][j]);
            }
        }
        return result;
    }

    // "-"
    public Matrix subtract(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Матрицы должны иметь одинаковый размер.");
        }

        Matrix result = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = this.data[i][j].subtract(other.data[i][j]);
            }
        }
        return result;
    }

    // "*"
    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("The number of columns of the first matrix must be equal to the number of rows of the second matrix.");
        }

        Matrix result = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                for (int k = 0; k < this.cols; k++) {
                    result.data[i][j] = result.data[i][j].add(this.data[i][k].multiply(other.data[k][j]));
                }
            }
        }
        return result;
    }

    public Matrix divide(Matrix other) {
        if (this.rows != other.cols || this.cols != other.rows) {
            throw new IllegalArgumentException("The number of columns of the first matrix must be equal to the number of columns of the second matrix.");
        }

        ComplexNumber detOther = other.determinant();
        if ((detOther.getX() == 0.0) && (detOther.getY() == 0.0)) {
            throw new IllegalArgumentException("Determinant for second matrix = 0. Can't count inverse matrix for seconde matrix.");
        }

        Matrix minorMatrixOther = new Matrix(other.rows, other.cols);
        for (int i = 0; i < other.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                minorMatrixOther.data[i][j] = other.minor(other.data, i, j);
            }
        }
        Matrix inverseMinorMatrixOther = minorMatrixOther.transposeMatrix();
        // divide inverseMinorMatrixOther to detOther
        Matrix inverseMatrixOther = new Matrix(inverseMinorMatrixOther.rows, inverseMinorMatrixOther.cols);
        for (int i = 0; i < inverseMinorMatrixOther.rows; i++) {
            for (int j = 0; j < inverseMinorMatrixOther.cols; j++) {
                inverseMatrixOther.data[i][j] = inverseMinorMatrixOther.data[i][j].divide(detOther);
            }
        }
        return this.multiply(inverseMatrixOther);
    }

    // determinant counting
    public ComplexNumber determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("The matrix must be square.");
        }

        return determinantRec(data);
    }

    // counting determinant recursively
    private ComplexNumber determinantRec(ComplexNumber[][] matrix) {
        int n = matrix.length;
        ComplexNumber help = new ComplexNumber();
        ComplexNumber det = new ComplexNumber();
        if (n == 1) {
            det = matrix[0][0];
            return det;
        }
        // a b
        // c d
        if (n == 2) {
            // (a + d) - (c + b)
            det = (matrix[0][0].multiply(matrix[1][1])).subtract(matrix[0][1].multiply(matrix[1][0]));
            return det;
        }
        //when multiplying, we always multiply all elements of 0 row by their minor
        for (int col = 0; col < n; col++) {
            help = matrix[0][col].multiply(minor(matrix,0,  col));
            det = det.add(help);
        }
        return det;
    }

    // counting minor
    // row added because of it's usage in method divide for matrix
    private ComplexNumber minor(ComplexNumber[][] matrix, int row, int col) {
        Matrix Minor = new Matrix(matrix.length - 1, matrix.length - 1);
        Minor.cutMatrix(matrix, row, col);
        ComplexNumber detMinor = determinantRec(Minor.data); // det for minor
        // for find "+" or "-"
        // sign "+"
        if ((col+row) % 2 == 0)
            return detMinor;
        // sign "-"
        // There are many conditions because of nice printing (not include -0.0 in any place)
        else {
            if ((detMinor.getX() != 0.0) && (detMinor.getY() != 0.0))
                return detMinor.multiply(new ComplexNumber(-1));
            if ((detMinor.getX() != 0.0) && (detMinor.getY() == 0.0)) {
                detMinor.setX(detMinor.getX() * (-1));
                return detMinor;
            }
            if ((detMinor.getX() == 0.0) && (detMinor.getY() != 0.0)){
                detMinor.setY(detMinor.getY() * (-1));
                return detMinor;
            }
            return detMinor;// if in detMinor x = 0.0 and y = 0.0
        }
    }

    private ComplexNumber[][] cutMatrix(ComplexNumber[][] matrix, int row, int col) {
        int n = matrix.length; //doesn't matter rows or cols because matrix is squared
        int minorRow = 0;
        for (int i = 0; i < n; i++) {
            if (i != row) {
                int minorCol = 0; // update to 0
                for (int j = 0; j < n; j++) {
                    if (j != col)
                        this.data[minorRow][minorCol++] = matrix[i][j];
                }
                minorRow++;
            }
        }
        return this.data;
    }

    private Matrix transposeMatrix () {
        Matrix Minor = new Matrix(this.rows , this.cols);
        for (int i = 0; i < Minor.rows; i++) {
            for (int j = 0; j < Minor.cols; j++) {
                Minor.data[i][j] = this.data[j][i];
            }
        }
        return Minor;
    }

    // Input matrix
    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(this.data[i][j] + " ");
            }
            System.out.println();
        }
    }
}
