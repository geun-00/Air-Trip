package project.infrastructure.time;

import project.common.domain.StayDatePolicy;

import java.time.LocalDate;

public interface StayDatePolicyProvider {

    StayDatePolicy getStayDatePolicy(LocalDate date);

    StayDatePolicy todayStayDatePolicy();
}
