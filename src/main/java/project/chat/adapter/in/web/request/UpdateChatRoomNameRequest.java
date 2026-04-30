package project.chat.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateChatRoomNameRequest(
        @NotBlank String customName,
        Long otherMemberId
) {
}
