package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Uploader s3Uploader;
    //사진 업로드
    @RequestMapping(value = "/api/image", method = RequestMethod.POST)
    public ResponseDto<?> upload(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String s3Image = s3Uploader.upload(multipartFile, "static");
        return ResponseDto.success(s3Image);
    }
}