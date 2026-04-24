package project.member.application.in.command.model;

import org.springframework.web.multipart.MultipartFile;

public record EditMyProfileCommand(
        Long memberId,
        MultipartFile imageFile,
        String name,
        String aboutMe,
        boolean profileImageChanged
) {
}
