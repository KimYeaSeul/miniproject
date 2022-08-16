package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.service.CommentService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성
  @PostMapping("/api/post/{id}/comment")
  public ResponseDto<?> createComment(@RequestBody CommentRequestDto requestDto,
                                      @PathVariable int id,
                                      HttpServletRequest request) {
    // @RequestHeader(value="Authentication") String accept,
    return commentService.createComment(requestDto, (long) id, request);
  }

//  @GetMapping("/api/comment/{postId}")
//  public ResponseDto<?> getAllComments(@RequestParam Long postId) {
//    return commentService.getAllCommentsByPost(postId);
//  }

  // 댓글 수정
  @PutMapping("/api/post/{id}/{commentId}")
  public ResponseDto<?> updateComment(@PathVariable Long id,
                                      @PathVariable Long commentId,
                                      @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.updateComment(id, commentId, requestDto, request);
  }

  // 댓글 삭제
  @DeleteMapping("/api/post/{id}/{commentId}")
  public ResponseDto<?> deleteComment(@PathVariable Long id, @PathVariable Long commentId,
                                      HttpServletRequest request) {
    return commentService.deleteComment(id, commentId, request);
  }
}
