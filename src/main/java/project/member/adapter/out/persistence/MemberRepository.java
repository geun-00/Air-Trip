package project.member.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import project.member.domain.SocialType;
import project.member.domain.Member;

import java.util.Optional;

// TODO : 포트, 어댑터 분리
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndSocialType(String email, SocialType socialType);
}