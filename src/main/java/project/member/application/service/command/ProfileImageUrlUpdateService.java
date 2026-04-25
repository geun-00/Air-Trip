package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.member.application.out.command.LoadMemberPort;
import project.member.application.out.command.SaveMemberPort;
import project.member.domain.Member;

@Service
@RequiredArgsConstructor
public class ProfileImageUrlUpdateService {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;

    @Transactional
    public void update(Long memberId, String profileImageUrl) {
        Member member = loadMemberPort.loadById(memberId);
        member.updateProfileUrl(profileImageUrl);
        saveMemberPort.save(member);
    }
}
