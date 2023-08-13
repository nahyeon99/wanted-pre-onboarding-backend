package wanted.preonboarding.boardspring.docs;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import wanted.preonboarding.boardspring.exception.CustomException;

import java.lang.reflect.Method;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static wanted.preonboarding.boardspring.exception.ExceptionCode.SERVER_ERROR;

@Slf4j
@Aspect
@Component
public class AopLogging {

    @Pointcut("execution(* wanted.preonboarding..*Controller.*(..))")
    private void apiTimer(){}

    @Pointcut("execution(public * wanted.preonboarding..*Service..*(..))")
    private void methodFromService() {
    }

    @Around("apiTimer()")
    public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        final Class className = signature.getDeclaringType();
        final Method method = signature.getMethod();

        log.info("========= [API START] {} {} API =========", className.getSimpleName(), method.getName());
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed();
        stopWatch.stop();

        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        log.info("========= [API FINISH] {} {} API | {} ms =========",
                className.getSimpleName(),
                method.getName(),
                totalTimeMillis);
        return proceed;
    }

    @AfterThrowing(value = "methodFromService()", throwing = "exception")
    public void logAfterThrowingFromService(JoinPoint joinPoint, CustomException exception) {

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            Class className = signature.getDeclaringType();
            Method method = signature.getMethod();

            log.error("[ERROR] {} | {} | throwing = {}",
                    className.getSimpleName(),
                    method.getName(),
                    exception.getExceptionCode().name());

            if (exception.getExceptionCode().getHttpStatus().equals(INTERNAL_SERVER_ERROR)) {
                if (exception.getException() != null)
                    log.error("***** SERVER ERROR DESCRIPTION *****", exception.getException());
            }
        } catch (Exception e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }
}
