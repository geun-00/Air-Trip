package project.accommodation.application.in.query.model;

import java.util.List;

public record DetailImageView(
        String thumbnail,
        List<String> others
) {
}
