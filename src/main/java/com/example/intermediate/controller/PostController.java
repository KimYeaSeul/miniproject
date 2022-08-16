package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    //게시글 작성
    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
                                     HttpServletRequest request) {
        System.out.println("requestDto : " + requestDto);
        return postService.createPost(requestDto, request);
    }

    //게시글 상세 조회
    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long id,
                                  HttpServletRequest request) {
        return postService.getPost(id,request);
    }

//    @RequestMapping(value = "/api/post/{postId}", method = RequestMethod.GET)
//    public ResponseDto<?> getPost(@PathVariable Long postId,
//                                  @RequestParam("commentsNum") Integer commentsNum,
//                                  @RequestParam(value = "pageLimit", defaultValue = "5") Integer pageLimit) {
//        return postService.getPost(postId, commentsNum, pageLimit);
//    }
    //게시글 상세 조회 댓글 분리
    @RequestMapping(value = "/api/post/{id}/comments", method = RequestMethod.GET)
    public ResponseDto<?> getAllCommentsById(@PathVariable Long id,
                                                 @RequestParam("commentsNum") Integer commentsNum,
                                                 @RequestParam(value = "pageLimit",defaultValue = "5") Integer pageLimit,
                                             HttpServletRequest request){
        return postService.getAllCommentsById(id, commentsNum, pageLimit, request);
    }

    //  @GetMapping("/api/comment/{postId}")
//  public ResponseDto<?> getAllComments(@RequestParam Long postId) {
//    return commentService.getAllCommentsByPost(postId);
//  }


    //게시글 전체 조회
    @RequestMapping(value = "/api/posts", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam(value = "pageLimit", defaultValue = "5") Integer pageLimit){
        return postService.getAllPost(pageNum, pageLimit);
    }

    //게시글 수정
    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(id, requestDto, request);
    }

    //게시글 삭제
    @RequestMapping(value = "/api/post/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long id,
                                     HttpServletRequest request) {
        return postService.deletePost(id, request);
    }

}
