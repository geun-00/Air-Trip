package project.member.application.event;

import org.springframework.web.multipart.MultipartFile;

public record MemberProfileImageChangedEvent(
        Long memberId,
        String oldImageUrl,
        MultipartFile newImageFile) {
}