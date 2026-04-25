package project.member.domain;

public record MemberName(String value) {

    public MemberName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("member name must not be blank");
        }

        this.value = value.trim();
    }
}
