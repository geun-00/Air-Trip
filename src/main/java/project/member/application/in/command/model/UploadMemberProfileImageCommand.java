package project.member.application.in.command.model;

public record UploadMemberProfileImageCommand(
        Long memberId,
        String oldImageUrl,
        ProfileImageSource source
) {
}
