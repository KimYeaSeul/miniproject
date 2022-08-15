package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OAtuhController {
    private final MemberService memberService;

    @Autowired
    OAtuhController(MemberService memberService){
        this.memberService = memberService;
    }
    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.redirect.url}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.redirect.url}")
    private String KAKAO_REDIRECT_URI;

    private final String RESPONSE_TYPE = "code";

    @GetMapping("/user/google/login")
    public String googleLogin(){
        String SCOPE = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
        String ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
        return "redirect:" + ENDPOINT + "?client_id=" + GOOGLE_CLIENT_ID + "&redirect_uri=" + GOOGLE_REDIRECT_URI
                + "&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPE;
    }

    @ResponseBody
    @GetMapping("/user/google/callback")
    public ResponseDto<?> oauthLogin(@RequestParam String code, HttpServletResponse response) {
        return memberService.googleLogin(code, response);
    }

    @GetMapping("/user/kakao/login")
    public String kakaoLogin(){
        String ENDPOINT = "https://kauth.kakao.com/oauth/authorize";
        return "redirect:" + ENDPOINT + "?client_id=" + KAKAO_CLIENT_ID + "&redirect_uri=" +
                KAKAO_REDIRECT_URI + "&response_type=" + RESPONSE_TYPE;
    }

    @ResponseBody
    @GetMapping("/user/kakao/callback")
    public ResponseDto<?> kakaoLogin(@RequestParam String code, HttpServletResponse response){
        return memberService.kakaoLogin(code, response);
    }
}
