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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseDto<?> usernameCheck(@RequestBody @Valid String username){
    return memberService.checkUsername(username);
  }

  @PostMapping("/user/nickname")
  public ResponseDto<?> nicknameCheck(@RequestBody @Valid String nickname){
    return memberService.checkNickname(nickname);
  }

  @GetMapping("/user/kakao/callback")
  public ResponseDto<?> kakaoLogin(@RequestParam String code, HttpServletResponse response){
    return memberService.kakaoLogin(code, response);
  }

  @Value("${google.client.id}")
  private String CLIENT_ID;
  @Value("${google.redirect.url}")
  private String REDIRECT_URI;

  @GetMapping("/user/google/login")
  public String googleLogin() {
    String RESPONSE_TYPE = "code";
    String SCOPE = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
    String ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    return "redirect:" + ENDPOINT + "?client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI
            + "&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPE;
  }

  @GetMapping("/user/google/callback")
  public ResponseDto<?> oauthLogin(@RequestParam String code, HttpServletResponse response) {
    return memberService.googleLogin(code, response);
  }
}
