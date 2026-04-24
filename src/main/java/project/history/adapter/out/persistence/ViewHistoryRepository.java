package project.history.adapter.out.persistence;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.history.domain.ViewHistory;

import java.time.LocalDateTime;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    @Modifying
    @Query("""
            UPDATE ViewHistory vh
            SET vh.viewedAt = :now
            WHERE vh.accommodation.id = :accommodationId
            AND vh.member.id = :memberId
            """
    )
    int updateViewedAt(@Param("accommodationId") Long accommodationId,
                       @Param("memberId") Long memberId,
                       @Param("now") LocalDateTime now);
}