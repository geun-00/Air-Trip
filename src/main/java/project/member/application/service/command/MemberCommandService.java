package project.member.application.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.application.event.MemberImageUploadEvent;
import project.member.application.event.MemberProfileImageChangedEvent;
import project.member.application.in.command.EditMyProfileUseCase;
import project.member.application.in.command.RegisterMemberUseCase;
import project.member.application.in.command.RegisterSocialMemberUseCase;
import project.member.application.in.command.model.EditMyProfileCommand;
import project.member.application.in.command.model.RegisterMemberCommand;
import project.member.application.in.command.model.RegisterSocialMemberCommand;
import project.member.application.out.command.LoadMemberPort;
import project.member.application.out.command.SaveMemberPort;
import project.member.domain.Member;
import project.member.domain.SocialType;
import project.member.domain.support.RestMemberCreateSpec;
import project.member.domain.support.SocialMemberCreateSpec;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService implements RegisterMemberUseCase,
                                             RegisterSocialMemberUseCase,
                                             EditMyProfileUseCase {

    private final SaveMemberPort saveMemberPort;
    private final LoadMemberPort loadMemberPort;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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

        if (loadMemberPort.existsByEmailAndSocialType(command.email(), socialType)) {
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
    public EditProfileResponse editMyProfile(EditMyProfileCommand command) {
        Member member = loadMemberPort.loadById(command.memberId());

        if (command.profileImageChanged()) {
            eventPublisher.publishEvent(new MemberProfileImageChangedEvent(command.memberId(), member.getProfileUrl(), command.imageFile()));
        }

        member.updateProfile(command.name(), command.aboutMe());
        saveMemberPort.save(member);

        return new EditProfileResponse(member.getName(), member.getProfileUrl(), member.getAboutMe());
    }

    private void validateExistsEmail(String email) {
        if (loadMemberPort.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
