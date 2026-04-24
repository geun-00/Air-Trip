package project.reservation.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

public abstract class ReservationExceptions {

    public static BusinessException notFoundById(Long reservationId) {
        return new BusinessException(
                ErrorCode.ENTITY_NOT_FOUND,
                "id=" + reservationId + " 예약 조회 실패"
        );
    }

}
