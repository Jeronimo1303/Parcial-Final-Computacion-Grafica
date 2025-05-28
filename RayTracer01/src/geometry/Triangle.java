package geometry;

import math.Vector4;

public class Triangle implements IntersectableObject {
    Vector4 Vector_a;
    Vector4 Vector_b;
    Vector4 Vector_c;

    Vector4 transformed_a;
    Vector4 transformed_b;
    Vector4 transformed_c;

    double colorIndex;
    double materialIndex;

    double d;
    Vector4 n;

    public Triangle(int x, int y, int z, int colorIndex, int materialIndex) {

    }

    public Solution intersect(Ray ray) {
        int Alpha;
        int Beta;
        int Gamma;

        double num = -(this.d + Vector4.dotProduct(ray.origin, this.n));
        double denom = Vector4.dotProduct(n, ray.direction);
        if (denom == 0)
            return null;
        double s = num / denom;

        return null;
    }

    @Override
    public void setCamera() {
        // TODO Auto-generated method stub

    }

}
