package project.member.application.in.query;

import project.member.application.in.query.model.DefaultProfileView;

public interface GetMyProfileQueryUseCase {

    DefaultProfileView getMyProfile(Long memberId);
}
