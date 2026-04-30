package project.member.domain;

public interface PasswordMatcher {

    boolean matches(String rawPassword, String encodedPassword);
}
