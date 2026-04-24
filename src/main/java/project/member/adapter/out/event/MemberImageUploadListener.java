package project.member.adapter.out.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import project.member.application.event.MemberImageUploadEvent;
import project.member.application.event.MemberProfileImageChangedEvent;
import project.member.application.in.command.UploadMemberProfileImageUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberImageUploadListener {

    private final UploadMemberProfileImageUseCase uploadMemberProfileImageUseCase;

    @EventListener
    public void handleMemberImageUploadEvent(MemberImageUploadEvent event) {
        uploadMemberProfileImageUseCase.upload(event.memberId(), event.imageUrl());
    }

    @EventListener
    public void handleMemberImageUploadEvent(MemberProfileImageChangedEvent event) {
        uploadMemberProfileImageUseCase.uploadAndDeleteOrigin(event.memberId(), event.oldImageUrl(), event.newImageFile());
    }
}
