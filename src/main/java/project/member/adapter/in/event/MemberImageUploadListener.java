package project.member.adapter.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.member.application.event.MemberImageUploadEvent;
import project.member.application.event.MemberProfileImageChangedEvent;
import project.member.application.in.command.UploadMemberProfileImageUseCase;
import project.member.application.in.command.model.ProfileImageSource;
import project.member.application.in.command.model.UploadMemberProfileImageCommand;

@Component
@RequiredArgsConstructor
public class MemberImageUploadListener {

    private final UploadMemberProfileImageUseCase uploadMemberProfileImageUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberImageUploadEvent event) {
        uploadMemberProfileImageUseCase.upload(new UploadMemberProfileImageCommand(
                event.memberId(),
                null,
                ProfileImageSource.url(event.imageUrl())
        ));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MemberProfileImageChangedEvent event) {
        uploadMemberProfileImageUseCase.upload(new UploadMemberProfileImageCommand(
                event.memberId(),
                event.oldImageUrl(),
                event.newImageFile() == null
                        ? ProfileImageSource.empty()
                        : ProfileImageSource.file(event.newImageFile())
        ));
    }
}
