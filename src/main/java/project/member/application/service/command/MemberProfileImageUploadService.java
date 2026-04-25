package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import project.common.exception.ImageUploadException;
import project.member.application.in.command.UploadMemberProfileImageUseCase;
import project.common.application.model.UploadFile;
import project.member.application.out.command.LoadMemberPort;
import project.member.application.out.command.ManageMemberProfileImagePort;
import project.member.application.out.command.SaveMemberPort;
import project.member.domain.Member;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberProfileImageUploadService implements UploadMemberProfileImageUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final ManageMemberProfileImagePort manageMemberProfileImagePort;

    @Transactional
    @Override
    public void upload(Long memberId, String imageUrl) {
        uploadProfileImage(memberId, key -> manageMemberProfileImagePort.uploadFromUrl(imageUrl, key));
    }

    @Transactional
    @Override
    public void uploadAndDeleteOrigin(Long memberId, String oldImageUrl, UploadFile newImageFile) {
        uploadProfileImage(memberId, key -> newImageFile != null ? manageMemberProfileImagePort.upload(newImageFile, key) : null);

        if (StringUtils.hasText(oldImageUrl)) {
            manageMemberProfileImagePort.delete(oldImageUrl);
        }
    }

    private void uploadProfileImage(Long memberId, FileUploadFunction uploadFunction) {
        Member member = loadMemberPort.loadById(memberId);
        String key = String.format("members/%s", UUID.randomUUID());

        try {
            member.updateProfileUrl(uploadFunction.upload(key));
            saveMemberPort.save(member);
            log.debug("Succeed to upload image to S3: memberId={}", member.getId());
        } catch (ImageUploadException e) {
            log.warn("Failed image upload for memberId={}. Continue without profile image.", member.getId(), e);
        }
    }

    @FunctionalInterface
    private interface FileUploadFunction {
        String upload(String key) throws ImageUploadException;
    }
}
