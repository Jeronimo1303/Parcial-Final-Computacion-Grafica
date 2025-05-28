package geometry;

import display.Scene;
import math.Matrix4x4;
import math.UVN4x4;
import math.Vector4;

public class Triangle implements IntersectableObject {
    Vector4 Vector_a;
    Vector4 Vector_b;
    Vector4 Vector_c;

    Vector4 transformed_a;
    Vector4 transformed_b;
    Vector4 transformed_c;

    Vector4 n;

    int colorIndex;
    int materialIndex;

    double Alpha;
    double Beta;
    double Gamma;
    double S;

    public Triangle(Vector4 a, Vector4 b, Vector4 c, int colorIndex, int materialIndex) {
        this.Vector_a = a;
        this.Vector_b = b;
        this.Vector_c = c;
        this.colorIndex = colorIndex;
        this.materialIndex = materialIndex;

        this.transformed_a = a;
        this.transformed_b = b;
        this.transformed_c = c;
    }

    public Solution intersect(Ray ray) {
        double[][] A = {
                { ray.direction.vector[0], transformed_b.vector[0] - transformed_a.vector[0],
                        transformed_c.vector[0] - transformed_a.vector[0] },
                { ray.direction.vector[1], transformed_b.vector[1] - transformed_a.vector[1],
                        transformed_c.vector[1] - transformed_a.vector[1] },
                { ray.direction.vector[2], transformed_b.vector[2] - transformed_a.vector[2],
                        transformed_c.vector[2] - transformed_a.vector[2] }
        };

        double[] b = {
                ray.origin.vector[0] - transformed_a.vector[0],
                ray.origin.vector[1] - transformed_a.vector[1],
                ray.origin.vector[2] - transformed_a.vector[2]
        };

        double[] X = Crammer(A, b);
        this.S = X[0];
        this.Beta = X[1];
        this.Gamma = X[2];
        this.Alpha = 1 - this.Beta - this.Gamma;

        if (S < 0)
            return null; // Ignore intersections behind ray origin

        if (n == null)
            return null;

        // n already normalized in computeNormalAndD()
        // just use n directly here

        if (Alpha >= 0 && Beta >= 0 && Gamma >= 0 &&
                Alpha <= 1 && Beta <= 1 && Gamma <= 1) {
            return new Solution(ray.evaluate(this.S), n, Scene.colors.get(colorIndex),
                    Scene.materials.get(materialIndex), this.S);
        }
        return null;
    }

    public void setCamera() {
        UVN4x4 uvn = Scene.camera.uvn;
        transformed_a = Matrix4x4.times(uvn, Vector_a);
        transformed_b = Matrix4x4.times(uvn, Vector_b);
        transformed_c = Matrix4x4.times(uvn, Vector_c);

        System.out.println("Triangle camera-space verts:");
        System.out.println("A: " + transformed_a);
        System.out.println("B: " + transformed_b);
        System.out.println("C: " + transformed_c);

        computeNormalAndD();
    }

    public void computeNormalAndD() {
        Vector4 p1p2 = Vector4.subtract(transformed_b, transformed_a);
        Vector4 p1p3 = Vector4.subtract(transformed_c, transformed_a);
        this.n = Vector4.crossProduct(p1p2, p1p3);
        this.n.normalize();
    }

    public static double determinantOfMatrix(double mat[][]) {
        double ans;
        ans = mat[0][0] * (mat[1][1] * mat[2][2] - mat[2][1] * mat[1][2])
                - mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0])
                + mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
        return ans;
    }

    public static double[][] copyMatrix(double[][] mat) {
        double[][] copy = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                copy[i][j] = mat[i][j];
            }
        }
        return copy;
    }

    public static double[] Crammer(double[][] A, double[] b) {
        double D = determinantOfMatrix(A);
        if (Math.abs(D) < 1e-6)
            return new double[] { Double.NaN, Double.NaN, Double.NaN };

        double[][] A_x = copyMatrix(A);
        A_x[0][0] = b[0];
        A_x[1][0] = b[1];
        A_x[2][0] = b[2];

        double[][] A_y = copyMatrix(A);
        A_y[0][1] = b[0];
        A_y[1][1] = b[1];
        A_y[2][1] = b[2];

        double[][] A_z = copyMatrix(A);
        A_z[0][2] = b[0];
        A_z[1][2] = b[1];
        A_z[2][2] = b[2];

        double D_x = determinantOfMatrix(A_x);
        double D_y = determinantOfMatrix(A_y);
        double D_z = determinantOfMatrix(A_z);

        return new double[] { D_x / D, D_y / D, D_z / D };
    }

}
