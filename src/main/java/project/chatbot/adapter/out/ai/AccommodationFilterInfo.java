package project.chatbot.adapter.out.ai;

public record AccommodationFilterInfo(
        String region,
        Integer minPrice,
        Integer maxPrice,
        Integer peopleCount
) {
}
