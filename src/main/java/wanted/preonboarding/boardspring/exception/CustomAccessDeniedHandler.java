package wanted.preonboarding.boardspring.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.ACCESS_DENIED_RESOURCE;

@Component("accessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static DefaultExceptionResponseDto EXCEPTION_RESPONSE =
            new DefaultExceptionResponseDto(
                    ACCESS_DENIED_RESOURCE.name(),
                    ACCESS_DENIED_RESOURCE.getMessage()
            );

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(403);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (OutputStream os = response.getOutputStream()) {
            mapper.writeValue(os, EXCEPTION_RESPONSE);
            os.flush();
        }
    }
}
