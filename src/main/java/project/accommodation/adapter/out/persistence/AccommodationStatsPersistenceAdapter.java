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
                JOIN (
                    SELECT accommodation_id
                    FROM reservations
                    WHERE updated_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE)
                    UNION
                    SELECT rs.accommodation_id
                    FROM reviews rv
                    JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                    WHERE rv.updated_at >= DATE_SUB(NOW(), INTERVAL 30 MINUTE)
                ) changed ON changed.accommodation_id = a.accommodation_id
                JOIN (
                    SELECT rs.accommodation_id,
                           COUNT(*)                    AS reservation_count
                    FROM reservations rs
                    WHERE rs.status != 'CANCELED'
                    GROUP BY rs.accommodation_id
                ) res_stats ON res_stats.accommodation_id = a.accommodation_id
                LEFT JOIN (
                    SELECT rs.accommodation_id,
                           COUNT(*)                    AS review_count,
                           ROUND(AVG(rv.rating), 2)   AS average_rating
                    FROM reviews rv
                    JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                    GROUP BY rs.accommodation_id
                ) rv_stats ON rv_stats.accommodation_id = a.accommodation_id
                SET
                    a.reservation_count = res_stats.reservation_count,
                    a.review_count      = COALESCE(rv_stats.review_count, 0),
                    a.average_rating    = COALESCE(rv_stats.average_rating, 0.0)
                """;
        em.createNativeQuery(sql)
          .executeUpdate();
    }

    @Override
    public void refreshAllStats() {
        String sql = """
                UPDATE accommodations a
                JOIN (
                    SELECT rs.accommodation_id,
                           COUNT(*)                    AS reservation_count
                    FROM reservations rs
                    WHERE rs.status != 'CANCELED'
                    GROUP BY rs.accommodation_id
                ) res_stats ON res_stats.accommodation_id = a.accommodation_id
                LEFT JOIN (
                    SELECT rs.accommodation_id,
                           COUNT(*)                    AS review_count,
                           ROUND(AVG(rv.rating), 2)   AS average_rating
                    FROM reviews rv
                    JOIN reservations rs ON rv.reservation_id = rs.reservation_id
                    GROUP BY rs.accommodation_id
                ) rv_stats ON rv_stats.accommodation_id = a.accommodation_id
                SET
                    a.reservation_count = res_stats.reservation_count,
                    a.review_count      = COALESCE(rv_stats.review_count, 0),
                    a.average_rating    = COALESCE(rv_stats.average_rating, 0.0)
                """;
        em.createNativeQuery(sql)
          .executeUpdate();
    }
}
