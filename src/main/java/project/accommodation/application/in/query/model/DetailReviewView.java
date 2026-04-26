package project.accommodation.application.in.query.model;

import java.time.LocalDateTime;

public record DetailReviewView(
        Long memberId,
        String memberName,
        String profileUrl,
        LocalDateTime memberCreatedDate,
        LocalDateTime reviewCreatedDate,
        double rating,
        String content
) {
}
