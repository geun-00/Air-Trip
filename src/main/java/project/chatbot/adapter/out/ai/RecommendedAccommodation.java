package project.chatbot.adapter.out.ai;

public record RecommendedAccommodation(
        Long id,
        String title,
        String price,
        int maxPeople
) { }
