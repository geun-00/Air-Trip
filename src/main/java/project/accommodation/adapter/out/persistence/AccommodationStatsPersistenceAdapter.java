package project.accommodation.adapter.out.persistence;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.accommodation.application.out.command.RefreshAccommodationStatsPort;

@Repository
@RequiredArgsConstructor
public class AccommodationStatsPersistenceAdapter implements RefreshAccommodationStatsPort {

    private final EntityManager em;

    @Override
    public void refreshTopStats() {
        em.createNativeQuery("DELETE FROM accommodation_stats").executeUpdate();
        em.flush();

        String sql = """
                INSERT INTO accommodation_stats (accommodation_id, area_code, area_name, title, average_rating, reservation_count, thumbnail_url)
                SELECT
                    ranked.accommodation_id,
                    parent_area.area_code,
                    parent_area.code_name,
                    ranked.title,
                    ranked.average_rating,
                    ranked.reservation_count,
                    ai.image_url
                FROM (
                    SELECT
                        a.accommodation_id,
                        a.title,
                        a.average_rating,
                        a.reservation_count,
                        child_area.parent_code AS area_code,
                        ROW_NUMBER() OVER (
                            PARTITION BY child_area.parent_code
                            ORDER BY a.reservation_count DESC, a.average_rating DESC
                        ) AS rn
                    FROM accommodations a
                    JOIN area_codes child_area ON child_area.area_code = a.area_code
                ) ranked
                JOIN area_codes parent_area ON parent_area.area_code = ranked.area_code
                JOIN accommodation_images ai
                ON ai.accommodation_id = ranked.accommodation_id
                AND ai.thumbnail = true
                WHERE ranked.rn <= 8
                """;

        em.createNativeQuery(sql).executeUpdate();
        em.flush();
    }

    @Override
    public void refreshRecentStats() {
        String sql = """
                UPDATE accommodations a
                SET
                    a.reservation_count = (
                        SELECT COUNT(*)
                        FROM reservations r
                        WHERE r.accommodation_id = a.accommodation_id
                          AND r.status != 'CANCELED'
                    ),
                    a.average_rating = COALESCE((
                        SELECT ROUND(AVG(rv.rating), 2)
                        FROM reviews rv
                        JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                        WHERE rs.accommodation_id = a.accommodation_id
                    ), 0.0)
                WHERE a.accommodation_id IN (
                    SELECT DISTINCT accommodation_id
                    FROM reservations
                    WHERE updated_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE)
                    UNION
                    SELECT DISTINCT rs.accommodation_id
                    FROM reviews rv
                    JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                    WHERE rv.updated_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE)
                )
                """;
        em.createNativeQuery(sql)
          .executeUpdate();
    }

    @Override
    public void refreshAllStats() {
        String sql = """
                UPDATE accommodations a
                SET
                    a.reservation_count = COALESCE((
                        SELECT COUNT(*)
                        FROM reservations r
                        WHERE r.accommodation_id = a.accommodation_id
                          AND r.status != 'CANCELED'
                    ), 0),
                    a.average_rating = COALESCE((
                        SELECT ROUND(AVG(rv.rating), 2)
                        FROM reviews rv
                        JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                        WHERE rs.accommodation_id = a.accommodation_id
                    ), 0.0)
                """;
        em.createNativeQuery(sql)
          .executeUpdate();
    }
}
