package wanted.preonboarding.boardspring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.*;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<DefaultExceptionResponseDto> handleCustomException(CustomException e) {

        return DefaultExceptionResponseDto.exceptionResponse(e.getExceptionCode());
    }

    /**
     * 400 Bad Request |
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        String responseMessage = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        String responseCode = null;

        if (responseMessage.contains("입력")) {
            responseCode = "FIELD_REQUIRED";
        } else if (responseMessage.contains("~")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_LENGTH_INVALID");
        } else if (responseMessage.contains("형식")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_CHARACTER_INVALID");
        } else if (responseMessage.contains("양수")) {
            responseCode = exception.getFieldError().getField().toUpperCase().concat("_NEGATIVEORZERO_INVALID");
        } else {
            responseCode = exception.getFieldError().getField().toUpperCase();
        }

        return ResponseEntity.status(status)
                .body(DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {

        String responseCode = null;
        String responseMessage = null;
        if (exception.getConstraintViolations().stream().findFirst().isPresent()) {
            ConstraintViolation<?> constraintViolation = exception.getConstraintViolations().stream().findFirst().get();

            int propertyPathSize = constraintViolation.getPropertyPath().toString().split("\\.").length;

            responseCode = constraintViolation.getPropertyPath()
                    .toString()
                    .split("\\.")[propertyPathSize - 1]
                    .toUpperCase();

            responseMessage = constraintViolation.getMessageTemplate();

            if (responseMessage.contains("입력")) {
                responseCode = "FIELD_REQUIRED";
            } else if (responseMessage.contains("~")) {
                responseCode = responseCode.concat("_LENGTH_INVALID");
            } else {
                responseCode = responseCode.toUpperCase();
            }
        } else {
            throw new CustomException(null, SERVER_ERROR);
        }

        return ResponseEntity.status(400)
                .body(DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

    /**
     * 405 Method Not Allowed
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request) {

        String responseCode = METHOD_NOT_ALLOWED.name();
        String responseMessage = METHOD_NOT_ALLOWED.getMessage();

        return ResponseEntity.status(status)
                .body(DefaultExceptionResponseDto.builder()
                        .responseCode(responseCode)
                        .responseMessage(responseMessage)
                        .build());
    }

}
