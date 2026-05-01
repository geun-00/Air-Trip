package project.member.adapter.out.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.member.adapter.out.redis.model.EmailVerification;
import project.member.application.out.command.ManageEmailVerificationTokenPort;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenAdapter implements ManageEmailVerificationTokenPort {

    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public void save(String token, Long memberId) {
        emailVerificationRepository.save(new EmailVerification(token, memberId));
    }

    @Override
    public Optional<Long> findMemberIdByToken(String token) {
        return emailVerificationRepository.findById(token)
                                          .map(EmailVerification::getMemberId);
    }

    @Override
    public void deleteByToken(String token) {
        emailVerificationRepository.deleteById(token);
    }
}
