package Obsidian.demo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {
    @Value("${secret.api.key}")
    private String secretApiKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String requestUri = httpServletRequest.getRequestURI();

        // Swagger 관련 요청은 필터 예외 처리
        if (requestUri.startsWith("/swagger-ui") || requestUri.startsWith("/v3/api-docs")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 요청 헤더에서 API Key 가져오기
        String requestApiKey = httpServletRequest.getHeader("X-API-KEY");

        if (requestApiKey == null || !requestApiKey.equals(secretApiKey)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.getWriter().write("Invalid API Key");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
