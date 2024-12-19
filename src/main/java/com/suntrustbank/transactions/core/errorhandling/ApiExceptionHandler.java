package com.suntrustbank.transactions.core.errorhandling;

import com.suntrustbank.transactions.core.dtos.BaseErrorResponse;
import com.suntrustbank.transactions.core.enums.ErrorCode;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String EXCEPTION_WAS_THROWN = "Exception was thrown:: ";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> handleInternalServerError(Exception e) {
        log.error(EXCEPTION_WAS_THROWN, e);
        return ResponseEntity.internalServerError()
            .body(mapErrors(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(GenericErrorCodeException.class)
    public ResponseEntity<BaseErrorResponse> handleGenericErrorCodeException(GenericErrorCodeException e) {
        log.error(EXCEPTION_WAS_THROWN, e);

        if (StringUtils.hasText(e.getMessage())) {
            return ResponseEntity.status(e.getHttpStatus())
                .body(mapErrorCode(e.getErrorCode(), e.getMessage()));
        }

        return ResponseEntity.status(e.getHttpStatus())
            .body(mapErrors(e.getErrorCode()));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException e) {
        log.error(EXCEPTION_WAS_THROWN, e);
        return ResponseEntity.status(e.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(e.getResponseBodyAsString());
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(EXCEPTION_WAS_THROWN, e);

        final List<FieldError> fieldErrors = e.getFieldErrors();
        final String[] errors = new String[fieldErrors.size()];

        for (int i = 0; i < fieldErrors.size(); i++) {
            final FieldError fieldError = fieldErrors.get(i);
            errors[i] = fieldError.getDefaultMessage();
        }

        return ResponseEntity.status(e.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapErrorCode(ErrorCode.BAD_REQUEST, errors));
    }

    private BaseErrorResponse mapErrors(ErrorCode... errorCodes) {
        return BaseErrorResponse.error(errorCodes,
            Arrays.stream(errorCodes).map(ErrorCode::getDescription).toList().toArray(new String[] {}));
    }

    private BaseErrorResponse mapErrorCode(final ErrorCode errorCode, String... errorMessages) {
        return BaseErrorResponse.error(new ErrorCode[] {errorCode}, errorMessages);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail detail) {
            return super.createResponseEntity(mapErrorCode(ErrorCode.BAD_REQUEST, detail.getDetail()),
                headers, statusCode, request);
        } else {
            return super.createResponseEntity(body, headers, statusCode, request);
        }
    }
}
