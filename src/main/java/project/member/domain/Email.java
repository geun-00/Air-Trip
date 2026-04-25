package project.member.domain;

import java.util.regex.Pattern;

public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }

        address = address.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("email is invalid");
        }
    }
}
