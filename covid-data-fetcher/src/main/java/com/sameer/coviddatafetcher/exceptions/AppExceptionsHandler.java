//package com.sameer.coviddatafetcher.exceptions;
//
//import java.util.Date;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//
//@ControllerAdvice
//@Slf4j
//public class AppExceptionsHandler {
//
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<?> handleGenericException(WebRequest webRequest, Exception exception) {
//
//  log.error(exception.toString());
//  log.error(webRequest.toString());
//    return new ResponseEntity<>(exception.getMessage(),new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
//  }
//
//  @ExceptionHandler(NullPointerException.class)
//  public ResponseEntity<?> handleGenericException1(WebRequest webRequest, Exception exception) {
//
//    log.error(exception.toString());
//    log.error(webRequest.toString());
//    return new ResponseEntity<>(exception.getMessage(),new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
//  }
//}
