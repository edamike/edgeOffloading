package at.ac.tuwien.offloading.uppaal.entity;

/**
 * @author edermic
 * @since 14.06.2019
 */
public enum ComputationIntensity
{
    LOW(1),
    MEDIUM(5),
    HIGH(10);

    private final int intensity;

    private ComputationIntensity(int s) {
        intensity = s;
    }

    public boolean equalsName(int otherIntensity) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return intensity == otherIntensity;
    }

    public int getValue() {
        return this.intensity;
    }
}
