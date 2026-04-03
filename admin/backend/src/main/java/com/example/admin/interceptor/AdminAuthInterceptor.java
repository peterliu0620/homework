package com.example.admin.interceptor;

import com.example.admin.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("未登录或登录已过期");
        }

        String token = authorization.substring(7);
        Claims claims = jwtService.parseToken(token);
        request.setAttribute("adminId", claims.get("adminId"));
        request.setAttribute("adminUsername", claims.getSubject());
        return true;
    }
}
