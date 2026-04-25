package project.member.application.in.command.model;

public record EditProfileResult(
        String name,
        String profileImageUrl,
        String aboutMe) {
}
