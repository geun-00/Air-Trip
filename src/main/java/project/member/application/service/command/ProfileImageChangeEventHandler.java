package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import project.common.application.model.UploadFile;
import project.member.application.event.MemberProfileImageChangedEvent;
import project.member.application.in.command.model.ProfileImageChange;

@Component
@RequiredArgsConstructor
public class ProfileImageChangeEventHandler implements ProfileImageChange.Handler {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void remove(Long memberId, String oldImageUrl) {
        eventPublisher.publishEvent(new MemberProfileImageChangedEvent(memberId, oldImageUrl, null));
    }

    @Override
    public void replace(Long memberId, String oldImageUrl, UploadFile file) {
        eventPublisher.publishEvent(new MemberProfileImageChangedEvent(memberId, oldImageUrl, file));
    }
}
