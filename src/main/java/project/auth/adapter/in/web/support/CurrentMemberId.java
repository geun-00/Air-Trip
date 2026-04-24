package project.auth.adapter.in.web.support;

import java.lang.annotation.*;

/**
 * 현재 사용자 ID를 얻을 수 있는 annotation
 * @see CurrentMemberIdArgumentResolver
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CurrentMemberId {
    boolean required() default true;
}
