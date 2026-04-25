package project.member.adapter.out.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.common.application.model.UploadFile;
import project.infrastructure.storage.S3Uploader;
import project.member.application.out.command.ManageMemberProfileImagePort;

@Component
@RequiredArgsConstructor
public class ManageMemberProfileImageAdapter implements ManageMemberProfileImagePort {

    private final S3Uploader s3Uploader;

    @Override
    public String uploadFromUrl(String imageUrl, String key) {
        return s3Uploader.uploadImage(imageUrl, key);
    }

    @Override
    public String upload(UploadFile file, String key) {
        return s3Uploader.uploadImage(file, key);
    }

    @Override
    public void delete(String imageUrl) {
        s3Uploader.deleteFile(imageUrl);
    }
}
