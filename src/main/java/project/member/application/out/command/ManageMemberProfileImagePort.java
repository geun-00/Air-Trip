package project.member.application.out.command;

import org.springframework.web.multipart.MultipartFile;

public interface ManageMemberProfileImagePort {

    String uploadFromUrl(String imageUrl, String key);

    String upload(MultipartFile file, String key);

    void delete(String imageUrl);
}
