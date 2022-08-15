package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MemberController {

  private final MemberService memberService;

  @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
  public ResponseDto<?> signup(@RequestBody @Valid MemberRequestDto requestDto) {
    return memberService.createMember(requestDto);
  }

  @RequestMapping(value = "/user/login", method = RequestMethod.POST)
  public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto,
      HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }

  @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
  public ResponseDto<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }

  @PostMapping("/user/username")
  public ResponseDto<?> usernameCheck(@RequestBody Map<String, String> body){
    // 이러케 해야 값이 들어오네요.!
    return memberService.checkUsername(body.get("username"));
  }

  @PostMapping("/user/nickname")
  public ResponseDto<?> nicknameCheck(@RequestBody Map<String, String> body){
    // 이러케 해야 값이 들어오네요.!
    return memberService.checkNickname(body.get("nickname"));
  }




}
