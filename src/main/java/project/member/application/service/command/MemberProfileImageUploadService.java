package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import project.common.application.model.UploadFile;
import project.common.exception.ImageUploadException;
import project.member.application.in.command.UploadMemberProfileImageUseCase;
import project.member.application.in.command.model.ProfileImageSource;
import project.member.application.in.command.model.UploadMemberProfileImageCommand;
import project.member.application.out.command.ManageMemberProfileImagePort;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberProfileImageUploadService implements UploadMemberProfileImageUseCase {

    private final ManageMemberProfileImagePort manageMemberProfileImagePort;
    private final ProfileImageUrlUpdateService profileImageUrlUpdateService;

    @Override
    public void upload(UploadMemberProfileImageCommand command) {
        String key = String.format("members/%s", UUID.randomUUID());

        try {
            String newImageUrl = command.source().uploadWith(key, new ProfileImageSourceHandler());
            profileImageUrlUpdateService.update(command.memberId(), newImageUrl);

            if (StringUtils.hasText(command.oldImageUrl())) {
                manageMemberProfileImagePort.delete(command.oldImageUrl());
            }

            log.debug("Succeed to update profile image: memberId={}", command.memberId());
        } catch (ImageUploadException e) {
            log.warn("Failed image upload for memberId={}. Continue without profile image.", command.memberId(), e);
        }
    }

    private class ProfileImageSourceHandler implements ProfileImageSource.Handler {

        @Override
        public String uploadFromUrl(String imageUrl, String key) {
            return manageMemberProfileImagePort.uploadFromUrl(imageUrl, key);
        }

        @Override
        public String uploadFile(UploadFile file, String key) {
            return manageMemberProfileImagePort.upload(file, key);
        }
    }
}
