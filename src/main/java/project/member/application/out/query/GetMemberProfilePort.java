package project.member.application.out.query;

import project.member.application.query.model.DefaultProfileView;

public interface GetMemberProfilePort {

    DefaultProfileView getDefaultProfile(Long memberId);
}
