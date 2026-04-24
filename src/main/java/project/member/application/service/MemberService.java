package project.member.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.common.adapter.in.web.response.PageResponse;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.adapter.in.web.request.EditProfileRequest;
import project.member.adapter.in.web.response.ChatMemberSearchResponse;
import project.member.adapter.in.web.response.ChatMembersSearchResponse;
import project.member.adapter.in.web.response.DefaultProfileResponse;
import project.member.adapter.in.web.response.EditProfileResponse;
import project.member.adapter.in.web.response.TripHistoryResponse;
import project.member.adapter.out.persistence.MemberQueryRepository;
import project.member.adapter.out.persistence.MemberRepository;
import project.member.adapter.out.persistence.model.DefaultProfileQueryDto;
import project.member.application.event.MemberProfileImageChangedEvent;
import project.member.domain.Member;
import project.member.domain.exception.MemberExceptions;

import java.util.List;

// TODO : Command / Query 분리
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberQueryRepository memberQueryRepository;

    @Transactional
    public EditProfileResponse editMyProfile(Long memberId, MultipartFile imageFile, EditProfileRequest profileReqDto) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> MemberExceptions.notFoundById(memberId));

        if (profileReqDto.isProfileImageChanged()) {
            eventPublisher.publishEvent(new MemberProfileImageChangedEvent(memberId, member.getProfileUrl(), imageFile));
        }

        member.updateProfile(profileReqDto.name(), profileReqDto.aboutMe());
        return new EditProfileResponse(member.getName(), member.getProfileUrl(), member.getAboutMe());
    }

    public DefaultProfileResponse getDefaultProfile(Long memberId) {
        DefaultProfileQueryDto profileQueryDto = memberQueryRepository.getDefaultProfile(memberId)
                                                                      .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
        return DefaultProfileResponse.from(profileQueryDto);
    }

    public ChatMembersSearchResponse findMembersByName(String name) {
        List<ChatMemberSearchResponse> members = memberQueryRepository.findMembersByName(name);
        return new ChatMembersSearchResponse(members);
    }

    public PageResponse<TripHistoryResponse> getTripsHistory(Long memberId, Pageable pageable) {
        Page<TripHistoryResponse> result = memberQueryRepository.getTripsHistory(memberId, pageable);

        return PageResponse.<TripHistoryResponse>builder()
                           .contents(result.getContent())
                           .pageNumber(pageable.getPageNumber())
                           .pageSize(pageable.getPageSize())
                           .total(result.getTotalElements())
                           .build();
    }

    private void validateExistsEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
