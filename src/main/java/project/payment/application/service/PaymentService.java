package project.payment.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.payment.adapter.out.api.PaymentClient;
import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.accommodation.domain.exception.AccommodationExceptions;
import project.payment.adapter.out.api.model.PaymentConfirmDto;
import project.payment.adapter.in.web.request.PaymentConfirmRequest;
import project.payment.adapter.in.web.response.PaymentResponse;
import project.payment.adapter.in.web.request.SavePaymentRequest;
import project.accommodation.domain.Accommodation;
import project.payment.domain.Payment;
import project.reservation.domain.Reservation;
import project.payment.domain.TempPayment;
import project.accommodation.adapter.out.persistence.AccommodationRepository;
import project.payment.adapter.out.persistence.PaymentRepository;
import project.reservation.adapter.out.persistence.ReservationRepository;
import project.reservation.adapter.out.persistence.ReservationQueryRepository;
import project.payment.adapter.out.persistence.TempPaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TempPaymentRepository tempPaymentRepository;
    private final AccommodationRepository accommodationRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    @Transactional
    public PaymentResponse confirmPayment(PaymentConfirmRequest paymentConfirmRequest, Long memberId) {
        String orderId = paymentConfirmRequest.orderId();
        Integer amount = paymentConfirmRequest.amount();
        Long reservationId = paymentConfirmRequest.reservationId();

        verifyTempPayment(orderId, amount);

        Reservation reservation = reservationRepository.findByIdWithPessimisticLock(reservationId).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        Accommodation accommodation = accommodationRepository.findByIdWithPessimisticLock(reservation.getAccommodation().getId())
                                                             .orElseThrow(() -> AccommodationExceptions.notFoundById(reservation.getAccommodation().getId()));
        if (!reservation.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        if (reservationQueryRepository.existsConfirmedReservation(
                accommodation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate())) {
            throw new BusinessException(ErrorCode.ALREADY_RESERVED);
        }

        PaymentConfirmDto paymentConfirmDTO = paymentConfirmRequest.convert();
        JsonNode response = paymentClient.confirmPayment(paymentConfirmDTO);

        paymentRepository.save(Payment.of(response, reservation));
        tempPaymentRepository.deleteById(orderId);
        reservation.confirm();

        String receiptUrl = response.get("receipt").get("url").asText(null);
        return new PaymentResponse(receiptUrl);
    }

    private void verifyTempPayment(String orderId, Integer amount) {
        TempPayment tempPayment = tempPaymentRepository.findById(orderId)
                                                       .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (tempPayment.notEqualsAmount(amount)) {
            throw new BusinessException(ErrorCode.NOT_EQUALS_AMOUNT);
        }
    }

    public void savePayment(SavePaymentRequest savePaymentRequestDTO) {
        String orderId = savePaymentRequestDTO.orderId();
        Integer amount = savePaymentRequestDTO.amount();

        tempPaymentRepository.save(TempPayment.builder().orderId(orderId).amount(amount).build());
    }
}
