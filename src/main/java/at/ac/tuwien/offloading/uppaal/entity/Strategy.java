package at.ac.tuwien.offloading.uppaal.entity;

public enum Strategy
{
    STATIC("STATIC"),
    PERIODIC("PERIODIC"),
    UNPREDICTABLE ("UNPREDICTABLE");

    private final String name;

    private Strategy(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
