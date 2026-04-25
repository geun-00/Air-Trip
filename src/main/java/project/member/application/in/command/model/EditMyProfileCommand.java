package project.member.application.in.command.model;

public record EditMyProfileCommand(
        Long memberId,
        ProfileImageChange profileImageChange,
        String name,
        String aboutMe
) {
}
