package project.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.accommodation.adapter.out.persistence.AccommodationImageRepository;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.accommodation.application.out.command.EvictAccommodationCommonInfoPort;
import project.accommodation.domain.Accommodation;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.member.adapter.out.persistence.MemberRepository;
import project.member.domain.Member;
import project.member.domain.exception.MemberExceptions;
import project.reservation.adapter.in.web.request.PostReservationRequest;
import project.reservation.adapter.in.web.response.PostReservationResponse;
import project.reservation.adapter.out.persistence.ReservationQueryRepository;
import project.reservation.adapter.out.persistence.ReservationRepository;
import project.reservation.domain.Reservation;
import project.reservation.domain.exception.ReservationExceptions;
import project.review.adapter.in.web.request.PostReviewReqDto;
import project.review.adapter.out.persistence.ReviewRepository;
import project.review.domain.Review;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationQueryRepository reservationQueryRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final EvictAccommodationCommonInfoPort evictAccommodationCommonInfoPort;

    @Transactional
    public PostReservationResponse postReservation(Long memberId, Long accommodationId, PostReservationRequest reqDto) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> MemberExceptions.notFoundById(memberId));
        //사용자 이메일 인증 안되어 있을 시 실패
        if (!member.getIsEmailVerified()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                                                             .orElseThrow(() -> AccommodationExceptions.notFoundById(accommodationId));
        //요청 기간에 이미 결제까지 이루어진 예약이 있으면 실패
        LocalDateTime from = reqDto.startDate().atStartOfDay();
        LocalDateTime to = reqDto.endDate().atTime(23, 59, 59);

        if (reservationQueryRepository.existsConfirmedReservation(accommodation.getId(), from, to)) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }

        Reservation reservation = reservationRepository.save(Reservation.createPending(member, accommodation, reqDto));
        String thumbnailUrl = accommodationImageRepository.findThumbnailUrl(accommodation);

        return PostReservationResponse.of(accommodation, thumbnailUrl, reservation);
    }

    @Transactional
    public void postReview(Long reservationId, PostReviewReqDto reqDto, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                                                       .orElseThrow(() -> ReservationExceptions.notFoundById(reservationId));
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> MemberExceptions.notFoundById(memberId));

        reviewRepository.save(Review.create(reqDto.rating().doubleValue(), reqDto.content(), reservation, member));
        evictAccommodationCommonInfoPort.evictAccommodationCommonInfo(reservation.getAccommodation().getId());
    }
}
