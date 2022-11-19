package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.domain.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.eunbinlib.api.auth.data.JwtProperties.USER_INFO;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
public class JwtAuthResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        User user = (User) webRequest.getAttribute(USER_INFO, SCOPE_REQUEST);

        return UserSession.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userType(user.getUserType())
                .build();
    }
}
