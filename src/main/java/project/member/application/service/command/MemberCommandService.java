package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.application.event.MemberImageUploadEvent;
import project.member.application.in.command.ManageMemberUseCase;
import project.member.application.in.command.RegisterAdminMemberUseCase;
import project.member.application.in.command.RegisterSocialMemberUseCase;
import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.EditProfileResult;
import project.member.application.in.command.model.ProfileImageChange;
import project.member.application.in.command.model.RegisterMemberCommand;
import project.member.application.in.command.model.RegisterSocialMemberCommand;
import project.member.application.out.command.ReadMemberPort;
import project.member.application.out.command.SaveMemberPort;
import project.member.domain.Member;
import project.member.domain.SocialType;
import project.member.domain.support.AdminMemberCreateSpec;
import project.member.domain.support.RestMemberCreateSpec;
import project.member.domain.support.SocialMemberCreateSpec;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements ManageMemberUseCase,
                                             RegisterAdminMemberUseCase,
                                             RegisterSocialMemberUseCase {

    private final SaveMemberPort saveMemberPort;
    private final ReadMemberPort readMemberPort;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final ProfileImageChange.Handler profileImageChangeHandler;

    @Override
    public void register(RegisterMemberCommand command) {
        validateExistsEmail(command.email());

        Member member = Member.createForRest(new RestMemberCreateSpec(
                command.name(),
                command.email(),
                command.number(),
                command.birthDate(),
                encodePassword(command.password())
        ));

        saveMemberPort.save(member);
    }

    @Override
    public void registerSocial(RegisterSocialMemberCommand command) {
        SocialType socialType = SocialType.from(command.provider());

        if (readMemberPort.existsByEmailAndSocialType(command.email(), socialType)) {
            return;
        }

        validateExistsEmail(command.email());

        Member member = Member.createForSocial(new SocialMemberCreateSpec(
                command.name(),
                command.email(),
                command.number(),
                command.birthDate(),
                encodePassword(command.password()),
                socialType
        ));

        saveMemberPort.save(member);

        if (command.imageUrl() != null) {
            eventPublisher.publishEvent(new MemberImageUploadEvent(member.getId(), command.imageUrl()));
        }
    }

    @Override
    public void registerAdmin(String email, String password) {
        if (readMemberPort.existsByEmail(email)) {
            return;
        }

        Member admin = Member.createAdmin(new AdminMemberCreateSpec(email, encodePassword(password)));
        saveMemberPort.save(admin);
    }

    @Override
    public EditProfileResult editMyProfile(EditMyProfileCommand command) {
        Member member = readMemberPort.getById(command.memberId());

        command.profileImageChange()
               .handleWith(command.memberId(), member.getProfileUrl(), profileImageChangeHandler);

        member.updateProfile(command.name(), command.aboutMe());
        saveMemberPort.save(member);

        return new EditProfileResult(member.getName(), member.getProfileUrl(), member.getAboutMe());
    }

    private void validateExistsEmail(String email) {
        if (readMemberPort.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
