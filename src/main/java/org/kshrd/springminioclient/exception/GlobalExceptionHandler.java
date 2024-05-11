package org.kshrd.springminioclient.exception;

import io.minio.errors.MinioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MinioException.class)
    ProblemDetail handleMinioException(MinioException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                e.getMessage());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
