package project.review.adapter.in.web.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PostReviewRequest(
        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("5.0")
        BigDecimal rating,

        @NotBlank
        @Size(max = 100)
        String content
) {
}
