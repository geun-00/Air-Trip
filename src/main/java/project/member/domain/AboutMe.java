package project.member.domain;

public record AboutMe(String value) {

    private static final int MAX_LENGTH = 500;

    public AboutMe {
        if (value == null) {
            throw new IllegalArgumentException("about me must not be null");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("about me must be 500 characters or less");
        }
    }
}
