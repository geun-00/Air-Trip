package project.member.domain;

import java.time.LocalDate;

public record BirthDate(LocalDate value) {

    public BirthDate {
        if (value == null) {
            throw new IllegalArgumentException("birth date must not be null");
        }

        if (!value.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("birth date must be in the past");
        }
    }
}
