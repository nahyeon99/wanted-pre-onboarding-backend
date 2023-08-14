package wanted.preonboarding.boardspring.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wanted.preonboarding.boardspring.exception.ExceptionCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.UNAUTHORIZED_MEMBER;

@Slf4j
@Component("JwtAuthenticationFilter")
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public CustomJwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    private final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtProvider.resolveToken(request);

            if (token != null) {
                if (jwtProvider.validateToken(token)) {
                    Authentication authentication = jwtProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("[ERROR] CustomJwtAuthenticationFilter | doFilterInternal | throwing = " + e);
            this.setExceptionResponse(response, UNAUTHORIZED_MEMBER);
        }

        filterChain.doFilter(request, response);
    }

    private void setExceptionResponse(
            HttpServletResponse response,
            ExceptionCode exceptionCode
    ){
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(exceptionCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(exceptionCode.toString(), exceptionCode.getMessage());
        try {
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("[THROWING] CustomJwtAuthenticationFilter | setExceptionResponse | throwing = {}", e.getMessage());
        }
    }

    @Data
    public static class ErrorResponse{
        private final String responseCode;
        private final String responseMessage;
    }
}