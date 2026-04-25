package project.member.application.out.command;

import project.common.application.model.UploadFile;

public interface ManageMemberProfileImagePort {

    String uploadFromUrl(String imageUrl, String key);

    String upload(UploadFile file, String key);

    void delete(String imageUrl);
}
