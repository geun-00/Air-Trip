package project.accommodation.domain;

public record ReservationCount(int value) {

    public static final ReservationCount ZERO = new ReservationCount(0);

    public ReservationCount {
        if (value < 0) {
            throw new IllegalArgumentException("reservation count must not be negative");
        }
    }
}
