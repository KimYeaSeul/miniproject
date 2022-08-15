package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.S3Uploader;
//import com.example.intermediate.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Uploader s3Uploader;

    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public ResponseDto<?> upload(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        String s3Image = s3Uploader.upload(multipartFile, "static");
        return ResponseDto.success(s3Image);
    }
//    private final AwsS3Service awsS3Service;
//
//    @RequestMapping(value ="/image", method = RequestMethod.POST)
//    public ResponseDto<?> uploadFile(@RequestPart("image") List<MultipartFile> multipartFile){
//        List<String> s3ImageList = awsS3Service.uploadFile(multipartFile);
//        return ResponseDto.success(s3ImageList);
//    }
}