package project.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google"),
    GITHUB("github"),
    NONE("none");

    private final String socialName;

    public static SocialType from(String registrationId) {
        return SocialType.valueOf(registrationId.toUpperCase());
    }
}
