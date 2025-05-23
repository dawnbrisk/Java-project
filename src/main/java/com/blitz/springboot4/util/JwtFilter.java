package com.blitz.springboot4.util;

import com.blitz.springboot4.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 跳过登录接口，不验证token
        if ("/login".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // 提取 token
                String token = authHeader.substring(7);

                // 从 token 中提取用户名
                String username = jwtUtil.extractUsername(token);

                // 如果用户名有效，且上下文中无已有认证信息
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 校验 token 是否有效
                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        // 构建认证对象并设置到上下文
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        // Token 校验失败，统一响应
                        sendErrorResponse(response, 403, "Token 无效");
                        return;
                    }
                }
            }

            // 放行
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, 403, "Token 已过期，请重新登录");
        } catch (Exception e) {
            sendErrorResponse(response, 500, "认证失败：" + e.getMessage());

        }
    }

    // 封装统一错误响应逻辑
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 状态始终为 200，由前端通过 code 判断
        ApiResponse<?> errorResponse = ApiResponse.error(code, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}