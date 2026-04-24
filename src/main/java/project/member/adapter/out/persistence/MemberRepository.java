package project.member.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.member.domain.Member;
import project.member.domain.SocialType;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndSocialType(String email, SocialType socialType);
}