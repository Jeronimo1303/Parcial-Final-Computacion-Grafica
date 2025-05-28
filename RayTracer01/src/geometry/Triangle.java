package geometry;

import math.Vector4;
import math.Matrix4x4;
import math.UVN4x4;
import display.Scene;

public class Triangle implements IntersectableObject {
    Vector4 p1, p2, p3;
    Vector4 transformedP1, transformedP2, transformedP3;
    int colorIndex;
    int materialIndex;

    public Triangle(Vector4 p1, Vector4 p2, Vector4 p3, int colorIndex, int materialIndex) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.transformedP1 = p1;
        this.transformedP2 = p2;
        this.transformedP3 = p3;
        this.colorIndex = colorIndex;
        this.materialIndex = materialIndex;
    }

    public Solution intersect(Ray ray) {
        // Möller–Trumbore intersection algorithm
        final double EPSILON = 1e-8;

        Vector4 edge1 = Vector4.subtract(transformedP2, transformedP1);
        Vector4 edge2 = Vector4.subtract(transformedP3, transformedP1);

        Vector4 h = Vector4.crossProduct(ray.direction, edge2);
        double a = Vector4.dotProduct(edge1, h);
        if (Math.abs(a) < EPSILON) {
            // Ray is parallel to the triangle
            return null;
        }

        double f = 1.0 / a;
        Vector4 s = Vector4.subtract(ray.origin, transformedP1);
        double u = f * Vector4.dotProduct(s, h);

        if (u < 0.0 || u > 1.0) {
            return null;
        }

        Vector4 q = Vector4.crossProduct(s, edge1);
        double v = f * Vector4.dotProduct(ray.direction, q);

        if (v < 0.0 || u + v > 1.0) {
            return null;
        }

        // Compute t to find where the intersection point is on the ray
        double t = f * Vector4.dotProduct(edge2, q);

        if (t > EPSILON) { // ray intersection
            Vector4 intersectionPoint = ray.evaluate(t);

            // Compute normal of the triangle
            Vector4 normal = Vector4.crossProduct(edge1, edge2);
            normal.normalize();

            // Return solution
            return new Solution(intersectionPoint, normal,
                    Scene.colors.get(colorIndex),
                    Scene.materials.get(materialIndex), t);
        }

        // No intersection
        return null;
    }

    public void setCamera() {
        UVN4x4 uvn = Scene.camera.uvn;
        transformedP1 = Matrix4x4.times(uvn, p1);
        transformedP2 = Matrix4x4.times(uvn, p2);
        transformedP3 = Matrix4x4.times(uvn, p3);
    }

    public void reset() {
        transformedP1 = p1;
        transformedP2 = p2;
        transformedP3 = p3;
    }

    // Optional: you can add a toString for debug purposes
    public String toString() {
        return "Triangle: p1=" + transformedP1 + " p2=" + transformedP2 + " p3=" + transformedP3;
    }

    // Main for testing (optional)
    public static void main(String[] args) {
        Vector4 a = new Vector4(0, 1, -5);
        Vector4 b = new Vector4(1, -1, -5);
        Vector4 c = new Vector4(-1, -1, -5);

        Triangle tri = new Triangle(a, b, c, 0, 0);
        tri.setCamera();

        Ray ray = new Ray(new Vector4(0, 0, 0), new Vector4(0, 0, -1));
        Solution sol = tri.intersect(ray);

        if (sol != null) {
            System.out.println("Intersection at: " + sol.intersectionPoint);
            System.out.println("Normal: " + sol.normal);
        } else {
            System.out.println("No intersection");
        }
    }
}
