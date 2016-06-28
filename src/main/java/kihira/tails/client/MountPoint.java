package kihira.tails.client;

public enum MountPoint {
    CHEST(0,0,0),
    HEAD(0,0,0);

    private final float x;
    private final float y;
    private final float z;

    MountPoint(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getXOffset() {
        return x;
    }

    public float getYOffset() {
        return y;
    }

    public float getZOffset() {
        return z;
    }
}
