package project.member.application.in.query;

import project.member.adapter.in.web.response.DefaultProfileResponse;

public interface GetMyProfileQueryUseCase {

    DefaultProfileResponse getMyProfile(Long memberId);
}
