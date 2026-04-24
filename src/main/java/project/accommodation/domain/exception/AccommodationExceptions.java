package project.accommodation.domain.exception;

import project.common.exception.BusinessException;
import project.common.exception.ErrorCode;

public abstract class AccommodationExceptions {

    public static BusinessException notFoundById(Long accommodationId) {
        return new BusinessException(
                ErrorCode.ACCOMMODATION_NOT_FOUND,
                "id=" + accommodationId + " 숙소 조회 실패"
        );
    }
}
