package project.auth.adapter.in.web.support;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import project.auth.adapter.in.security.principal.AuthenticatedMember;
import project.auth.exception.AuthException;
import project.common.exception.ErrorCode;

public class CurrentMemberIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentMemberId.class) &&
                Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        CurrentMemberId annotation = parameter.getParameterAnnotation(CurrentMemberId.class);
        Assert.notNull(annotation, "Cannot be empty CurrentMemberId");

        Authentication authentication = SecurityContextHolder.getContextHolderStrategy()
                                                             .getContext()
                                                             .getAuthentication();
        boolean required = annotation.required();

        if (authentication == null) {
            if (required) {
                throw new AuthException(ErrorCode.UNAUTHORIZED);
            }
            return null;
        }

        if (authentication.getPrincipal() instanceof AuthenticatedMember authenticatedMember) {
            return authenticatedMember.memberId();
        }

        if (required) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        return null;
    }
}
