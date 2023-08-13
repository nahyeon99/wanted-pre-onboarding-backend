package wanted.preonboarding.boardspring.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.UNAUTHORIZED_MEMBER;

@Component("AuthenticationEntryPoint")
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPointHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static DefaultExceptionResponseDto EXCEPTION_RESPONSE =
            new DefaultExceptionResponseDto(
                    UNAUTHORIZED_MEMBER.name(),
                    UNAUTHORIZED_MEMBER.getMessage()
            );

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (OutputStream os = response.getOutputStream()) {
            mapper.writeValue(os, EXCEPTION_RESPONSE);
            os.flush();
        }
    }

}
