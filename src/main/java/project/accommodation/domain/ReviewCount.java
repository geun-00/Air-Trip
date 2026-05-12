package project.accommodation.domain;

public record ReviewCount(int value) {

    public static final ReviewCount ZERO = new ReviewCount(0);

    public ReviewCount {
        if (value < 0) {
            throw new IllegalArgumentException("review count must not be negative");
        }
    }
}
