package wanted.preonboarding.boardspring.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.*;

@Slf4j
@Aspect
public class JwtExceptionHandler extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("[ERROR] JwtExceptionHandler | doFilterInternal | throwing = " + e);
            this.setExceptionResponse(response, UNAUTHORIZED_MEMBER);
        }
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
            log.error("[THROWING] ExceptionHandlerFilter | setExceptionResponse | throwing = {}", e.getMessage());
        }
    }

    @Data
    public static class ErrorResponse{
        private final String responseCode;
        private final String responseMessage;
    }
}
