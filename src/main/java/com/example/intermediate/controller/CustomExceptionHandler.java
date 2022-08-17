package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
        .getAllErrors()
        .get(0)
        .getDefaultMessage();

    return ResponseDto.fail("400", errorMessage);
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseDto<?> handleJsonProcessingException(JsonProcessingException exception){
    String errorMessage = "readTree Fail" + exception.getMessage();
    return ResponseDto.fail("500",errorMessage);
  }
}
