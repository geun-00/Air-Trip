package project.chatbot.adapter.out.ai.model;

public record RecommendedAccommodationRow(
        Long id,
        String title,
        String price,
        int maxPeople
) { }
