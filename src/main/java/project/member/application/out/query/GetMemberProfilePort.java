package project.member.application.out.query;

import project.member.application.in.query.model.DefaultProfileView;

public interface GetMemberProfilePort {

    DefaultProfileView getDefaultProfile(Long memberId);
}
