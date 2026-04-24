package project.member.application.command.model;

public record EditProfileResult(
        String name,
        String profileImageUrl,
        String aboutMe) {
}
