package project.member.application.out.query;

import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;

public interface GetMemberProfilePort {

    DefaultProfileQueryDto getDefaultProfile(Long memberId);
}
