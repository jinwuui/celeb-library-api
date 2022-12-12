package com.eunbinlib.api.auth.argumentresolver;

import com.eunbinlib.api.auth.data.UserSession;
import com.eunbinlib.api.auth.utils.AuthService;
import com.eunbinlib.api.auth.utils.AuthorizationExtractor;
import com.eunbinlib.api.auth.exception.type.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
public class UserSessionArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return new UnauthorizedException();
        }

        String accessToken = AuthorizationExtractor.extractToken(request);

        return authService.getSession(accessToken);
    }
}
