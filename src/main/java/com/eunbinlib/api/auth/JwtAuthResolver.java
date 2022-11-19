package com.eunbinlib.api.auth;

import com.eunbinlib.api.auth.data.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.eunbinlib.api.auth.data.JwtProperties.USERNAME;
import static com.eunbinlib.api.auth.data.JwtProperties.USER_TYPE;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
public class JwtAuthResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String userType = (String) webRequest.getAttribute(USER_TYPE, SCOPE_REQUEST);
        String username = (String) webRequest.getAttribute(USERNAME, SCOPE_REQUEST);

        return UserSession.builder()
                .userType(userType)
                .username(username)
                .build();
    }
}
