package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
        .getAllErrors()
        .get(0)
        .getDefaultMessage();

    return ResponseDto.fail("BAD_REQUEST", errorMessage);
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseDto<?> handleJsonProcessingException(JsonProcessingException exception){
    String errorMessage = "readTree Fail" + exception.getMessage();
    return ResponseDto.fail("500",errorMessage);
  }
  @ExceptionHandler(Exception.class)
  public ResponseDto<?> globalExceptions(Exception exception) {
    String getClass = String.valueOf(exception.getClass());
    String errorMessage = exception.getMessage();

    return ResponseDto.fail("GlobalExceptions_BAD_REQUEST", getClass+" : "+errorMessage);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseDto<?> globalExceptions(MaxUploadSizeExceededException exception) {
    String errorMessage = exception.getMessage();
    String getClass = String.valueOf(exception.getClass());

    return ResponseDto.fail("MaxUploadSizeExceededException_BAD_REQUEST", errorMessage);
  }
}
