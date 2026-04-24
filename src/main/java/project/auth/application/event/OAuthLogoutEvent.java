package project.auth.application.event;

import project.member.domain.SocialType;

public record OAuthLogoutEvent(SocialType socialType) {
}
