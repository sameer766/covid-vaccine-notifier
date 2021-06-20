package com.sameer.coviddatafetcher.exceptions;

import com.sameer.coviddatafetcher.model.OperationStatus;
import com.sameer.coviddatafetcher.model.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AppExceptionsHandler {


  @ExceptionHandler(value = {Exception.class})
  public Response handleGenericException(WebRequest webRequest,
                                         Exception exception) {
    return new Response(exception.getMessage(), OperationStatus.SYSTEM_ERROR.name());
  }
}
