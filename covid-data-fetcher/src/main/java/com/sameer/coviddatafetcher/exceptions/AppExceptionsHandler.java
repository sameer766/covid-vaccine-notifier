package com.sameer.coviddatafetcher.exceptions;

import com.sameer.coviddatafetcher.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class AppExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public Response handleGenericException(Exception exception, WebRequest webRequest) {

        log.error(exception.toString());
        log.error(webRequest.toString());
        return new Response(exception.getMessage(),HttpStatus.OK.toString());
    }

    @ExceptionHandler(NullPointerException.class)
    public Response handleGenericException1(NullPointerException exception, WebRequest webRequest) {
        log.error(exception.toString());
        log.error(webRequest.toString());
        return new Response(exception.getMessage(),HttpStatus.NOT_EXTENDED.toString());
    }
}
