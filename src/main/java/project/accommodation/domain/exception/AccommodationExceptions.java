package project.accommodation.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;
import project.common.domain.StayDatePolicy;

public abstract class AccommodationExceptions {

    public static BusinessException notFoundById(Long accommodationId) {
        return new BusinessException(
                ErrorCode.ACCOMMODATION_NOT_FOUND,
                "id=" + accommodationId + " 숙소 조회 실패"
        );
    }

    public static BusinessException priceNotFound(Long accommodationId, StayDatePolicy stayDatePolicy) {
        return new BusinessException(
                ErrorCode.ACCOMMODATION_NOT_FOUND,
                "id=%d, season=%s, dayType=%s 숙소 가격 조회 실패".formatted(accommodationId, stayDatePolicy.season(), stayDatePolicy.dayType())
        );
    }
}
