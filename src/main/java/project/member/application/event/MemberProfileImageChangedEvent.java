package project.member.application.event;

import project.common.application.model.UploadFile;

public record MemberProfileImageChangedEvent(
        Long memberId,
        String oldImageUrl,
        UploadFile newImageFile) {
}
