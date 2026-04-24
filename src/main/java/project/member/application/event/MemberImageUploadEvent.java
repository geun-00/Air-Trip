package project.member.application.event;

public record MemberImageUploadEvent(Long memberId, String imageUrl) {
}
