package project.member.adapter.out.persistence;

import org.springframework.data.repository.Repository;
import project.member.domain.Email;
import project.member.domain.Member;
import project.member.domain.SocialType;

import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);

    boolean existsByEmailAndSocialType(Email email, SocialType socialType);
}
