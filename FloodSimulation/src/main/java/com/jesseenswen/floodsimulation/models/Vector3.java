package com.jesseenswen.floodsimulation.models;

/**
 *
 * @author swenm_000
 */
public class Vector3<T> {
    public static Vector3<Float> Zero = new Vector3<>(0f, 0f, 0f);
    
    private T x;
    private T y;
    private T z;
    
    public Vector3() { }

    public Vector3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    public T getZ() {
        return z;
    }

    public void setZ(T z) {
        this.z = z;
    }
    
    @Override
    public String toString() {
        return "Vector3[x: " + x + "; y: " + y + "; z: " + z + "]";
    }
}
