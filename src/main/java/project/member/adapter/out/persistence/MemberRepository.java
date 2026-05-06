package project.member.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.member.domain.Email;
import project.member.domain.Member;
import project.member.domain.SocialType;

import java.util.Optional;

@JpaPersistenceRepository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);

    boolean existsByEmailAndSocialType(Email email, SocialType socialType);
}
