package project.chatbot.adapter.out.ai.model;

import project.common.domain.DayType;
import project.common.domain.Season;

public record AccommodationEmbeddingRow(
        Long accommodationId,
        String title,
        String description,
        int maxPeople,
        String address,
        String areaName,
        String sigunguName,
        Season season,
        DayType dayType,
        int price
) {
    public String getRegion() {
        return areaName + " " + sigunguName;
    }
}
