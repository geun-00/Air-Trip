package project.review.adapter.out.persistence;

import project.common.adapter.out.persistence.repository.JpaPersistenceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import project.review.domain.Review;

import java.util.Optional;

@JpaPersistenceRepository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByIdAndMemberId(Long id, Long memberId);
}