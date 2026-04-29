package project.member.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.member.application.out.command.LoadMemberPort;
import project.member.application.out.command.SaveMemberPort;
import project.member.domain.Email;
import project.member.domain.Member;
import project.member.domain.SocialType;
import project.member.domain.exception.MemberExceptions;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements LoadMemberPort, SaveMemberPort {

    private final MemberRepository memberRepository;

    @Override
    public Member loadById(Long memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
    }

    @Override
    public String loadMemberName(Long memberId) {
        return loadById(memberId).getName();
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(new Email(email));
    }

    @Override
    public boolean existsByEmailAndSocialType(String email, SocialType socialType) {
        return memberRepository.existsByEmailAndSocialType(new Email(email), socialType);
    }

    @Override
    @Transactional
    public Member save(Member member) {
        return memberRepository.save(member);
    }
}
