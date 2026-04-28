package project.accommodation.domain;

public record Rating(double value) {

    private static final double MIN = 0.0;
    private static final double MAX = 5.0;

    public static final Rating ZERO = new Rating(MIN);

    public Rating {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException("rating must be between 0.0 and 5.0");
        }
    }
}
