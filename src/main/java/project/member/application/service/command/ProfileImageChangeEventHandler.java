package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import project.member.application.in.command.UploadMemberProfileImageUseCase;
import project.member.application.in.command.model.ProfileImageChange;
import project.common.application.model.UploadFile;

@Component
@RequiredArgsConstructor
public class ProfileImageChangeEventHandler implements ProfileImageChange.Handler {

    private final ApplicationEventPublisher eventPublisher;
    private final UploadMemberProfileImageUseCase uploadMemberProfileImageUseCase;

    @Override
    public void remove(Long memberId, String oldImageUrl) {
//        eventPublisher.publishEvent(new MemberProfileImageChangedEvent(memberId, oldImageUrl, null));

        uploadMemberProfileImageUseCase.uploadAndDeleteOrigin(memberId, oldImageUrl, null);
    }

    @Override
    public void replace(Long memberId, String oldImageUrl, UploadFile file) {
//        eventPublisher.publishEvent(new MemberProfileImageChangedEvent(memberId, oldImageUrl, file));

        uploadMemberProfileImageUseCase.uploadAndDeleteOrigin(memberId, oldImageUrl, file);
    }
}
