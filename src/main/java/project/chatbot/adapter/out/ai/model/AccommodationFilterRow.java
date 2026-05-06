package project.chatbot.adapter.out.ai.model;

public record AccommodationFilterRow(
        String region,
        Integer minPrice,
        Integer maxPrice,
        Integer peopleCount
) {
}
