package project.member.adapter.in.web.response;

public record EditProfileResponse(
        String name,
        String profileImageUrl,
        String aboutMe) {
}
