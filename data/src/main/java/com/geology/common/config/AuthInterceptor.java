package com.geology.common.config;

import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.common.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 *
 * @author lqd
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(AuthStorage.TOKEN_KEY);
        if (StringUtils.hasLength(token)) {
            JwtUser jwtUser = TokenProvider.checkToken(token);
            // 是否认证通过
            if (jwtUser.isValid()) {
                // 保存授权信息
                AuthStorage.setUser(token, jwtUser);
                return true;
            }
        }

        if((!(request.getQueryString() == null) && request.getQueryString().contains("token")))
        {
            String tokenQuery = request.getQueryString().split("token=")[1];
            JwtUser jwtUser = TokenProvider.checkToken(tokenQuery);
            // 是否认证通过
            if (jwtUser.isValid()) {
                // 保存授权信息
                AuthStorage.setUser(token, jwtUser);
                return true;
            }
        }

        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("请先登录！");
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成清除授权信息
        AuthStorage.clearUser();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
