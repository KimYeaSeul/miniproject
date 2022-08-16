package com.example.intermediate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
  private boolean result;
  private T data;
  private Status status;

  public static <T> ResponseDto<T> success(T data) {
    return new ResponseDto<>(true, data, new Status("200","Success!"));
  }

  public static <T> ResponseDto<T> fail(String code, String message) {
    return new ResponseDto<>(false, null, new Status(code, message));
  }

  @Getter
  @AllArgsConstructor
  static class Status {
    private String code;
    private String message;
  }

}
