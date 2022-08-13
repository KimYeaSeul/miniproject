package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    //게시글 작성
    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
                                     HttpServletRequest request) {
        return postService.createPost(requestDto, request);
    }

    //게시글 상세 조회
    @RequestMapping(value = "/api/post/{postId}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }


    //게시글 전체 조회
    @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts() {
        return postService.getAllPost();
    }

    //게시글 수정
    @RequestMapping(value = "/api/post/{postId}", method = RequestMethod.PUT)
    public ResponseDto<?> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto postRequestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(postId, postRequestDto, request);
    }

    //게시글 삭제
    @RequestMapping(value = "/api/post/{postId}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long postId,
                                     HttpServletRequest request) {
        return postService.deletePost(postId, request);
    }

}
