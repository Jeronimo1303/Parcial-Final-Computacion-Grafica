package geometry;

import display.Scene;
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

    public Triangle(int x, int y, int z, int colorIndex, int materialIndex) {

    }

    public Solution intersect(Ray ray) {
        double[][] A = {
                { ray.direction.vector[0], (Vector_a.vector[0] - Vector_b.vector[0]),
                        Vector_a.vector[0] - Vector_c.vector[0] },
                { ray.direction.vector[1], (Vector_a.vector[1] - Vector_b.vector[1]),
                        Vector_a.vector[1] - Vector_c.vector[1] },
                { ray.direction.vector[2], (Vector_a.vector[2] - Vector_b.vector[2]),
                        Vector_a.vector[2] - Vector_c.vector[2] }
        };

        double[] b = {
                Vector_a.vector[0] - ray.origin.vector[0],
                Vector_a.vector[1] - ray.origin.vector[1],
                Vector_a.vector[2] - ray.origin.vector[2]
        };

        double[] X = Crammer(A, b);
        this.S = X[0];
        this.Beta = X[1];
        this.Gamma = X[2];
        this.Alpha = 1 - this.Beta - this.Gamma;
        n.normalize();
        // Check if the point is inside the triangle.
        if (0 >= this.Alpha && this.Alpha <= 1) {
            if (0 >= this.Beta && this.Beta <= 1) {
                if (0 >= this.Gamma && this.Gamma <= 1) {
                    return new Solution(ray.evaluate(this.S), n, Scene.colors.get(colorIndex),
                            Scene.materials.get(materialIndex), S);
                }
            }
        }

        return null;
    }

    public void setCamera() {
        // TODO Auto-generated method stub

    }

    public static double determinantOfMatrix(double mat[][]) {
        double ans;
        ans = mat[0][0] * (mat[1][1] * mat[2][2] - mat[2][1] * mat[1][2])
                - mat[0][1] * (mat[1][0] * mat[2][2] - mat[1][2] * mat[2][0])
                + mat[0][2] * (mat[1][0] * mat[2][1] - mat[1][1] * mat[2][0]);
        return ans;
    }

    public static double[] Crammer(double[][] A, double[] b) {
        double D = determinantOfMatrix(A);

        double[][] A_x = A;
        A_x[0][0] = b[0];
        A_x[1][0] = b[1];
        A_x[2][0] = b[2];

        double[][] A_y = A;
        A_y[0][1] = b[0];
        A_y[1][1] = b[1];
        A_y[2][1] = b[2];

        double[][] A_z = A;
        A_z[0][2] = b[0];
        A_z[1][2] = b[1];
        A_z[2][2] = b[2];

        double D_x = determinantOfMatrix(A_x);
        double D_y = determinantOfMatrix(A_y);
        double D_z = determinantOfMatrix(A_z);
        double[] results = new double[3];

        results[0] = D_x / D;
        results[1] = D_y / D;
        results[2] = D_z / D;
        return results;
    }
}
