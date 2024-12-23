package com.suntrustbank.transactions.core.errorhandling.exceptions;

import com.suntrustbank.transactions.core.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@RequiredArgsConstructor
public class GenericErrorCodeException extends RuntimeException {

    private final String message;
    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public GenericErrorCodeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getDescription();
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public static GenericErrorCodeException unauthenticated() {
        return new GenericErrorCodeException(ErrorCode.UN_AUTHENTICATED.getDescription(),
            ErrorCode.UN_AUTHENTICATED, HttpStatus.UNAUTHORIZED);
    }

    public static GenericErrorCodeException badRequest(String message) {
        return new GenericErrorCodeException(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public static GenericErrorCodeException notFound(String message) {
        return new GenericErrorCodeException(message, ErrorCode.BAD_REQUEST, HttpStatus.NOT_FOUND);
    }

    public static GenericErrorCodeException serverError() {
        return new GenericErrorCodeException(ErrorCode.INTERNAL_SERVER_ERROR.getDescription(),
            ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static GenericErrorCodeException unAuthorizedToken() {
        return new GenericErrorCodeException(ErrorCode.UN_AUTHENTICATED_TOKEN);
    }
}
