package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("api/my")
    public ResponseDto<?> myPage(HttpServletRequest request){
        return myPageService.showMypage(request);
    }
}
