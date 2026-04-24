package project.member.adapter.in.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditProfileRequest(
        @NotBlank
        String name,

        @Size(max = 500)
        String aboutMe,

        @NotNull
        Boolean isProfileImageChanged) {
}
