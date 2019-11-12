package at.ac.tuwien.offloading.euadatasets;

public enum Example {
    MELBOURNE_METROPOLIAN("MELBOURNE_METROPOLIAN"),
    SYNTHETIC_EXAMPLE_SMALL("SYNTHETIC_EXAMPLE_SMALL"),
    SYNTHETIC_EXAMPLE_MOTIVATING("SYNTHETIC_EXAMPLE_MOTIVATING");

    private final String name;

    private Example(String s) {
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
