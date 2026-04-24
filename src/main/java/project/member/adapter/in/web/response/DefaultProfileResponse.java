package project.member.adapter.in.web.response;

import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;

import java.time.LocalDate;

public record DefaultProfileResponse(
        String name,
        String profileImageUrl,
        LocalDate createdDate,
        String aboutMe,
        boolean isEmailVerified) {

    public static DefaultProfileResponse from(DefaultProfileQueryDto queryDto) {
        return new DefaultProfileResponse(
                queryDto.name(),
                queryDto.profileImageUrl(),
                queryDto.createdDateTime().toLocalDate(),
                queryDto.aboutMe(),
                queryDto.isEmailVerified()
        );
    }
}
