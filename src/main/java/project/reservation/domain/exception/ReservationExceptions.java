package project.reservation.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

public abstract class ReservationExceptions {

    private ReservationExceptions() {
    }

    public static BusinessException alreadyConfirmed(Long reservationId) {
        return new BusinessException(
                ErrorCode.ALREADY_CONFIRMED,
                "id=" + reservationId + " 예약이 이미 확정되었습니다"
        );
    }

    public static BusinessException notFoundById(Long reservationId) {
        return new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                "id=" + reservationId + " 예약 조회 실패"
        );
    }

    public static BusinessException notOwner(Long reservationId, Long memberId) {
        return new BusinessException(
                ErrorCode.ACCESS_DENIED,
                String.format("reservationId=%d, memberId=%d 예약 소유자 아님", reservationId, memberId)
        );
    }
}
