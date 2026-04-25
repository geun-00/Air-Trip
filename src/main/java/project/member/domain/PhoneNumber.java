package project.member.domain;

import java.util.regex.Pattern;

public record PhoneNumber(String value) {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^010\\d{8}$");

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("phone number must not be blank");
        }

        value = value.replace("-", "").trim();

        if (!PHONE_NUMBER_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("phone number is invalid");
        }
    }
}
