package project.accommodation.domain;

public record Capacity(Integer value) {

    public Capacity {
        if (value == null) {
            throw new IllegalArgumentException("capacity must not be null");
        }

        if (value <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
    }
}
