package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(CommentRequestDto requestDto, Long id, HttpServletRequest request) {
//     header에 token이 있는지 확인
    if (request.getHeader("RefreshToken") == null || request.getHeader("Authorization") == null) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

//     refresh token이 유효한지 확인
    Member member = validateMember(request);
    if (member == null) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = postService.isPresentPost(id);
    if (post == null) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
    }

//    Member member = memberRepository.findById(1L).orElseGet(Member::new);

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .content(requestDto.getContent())
        .build();
    commentRepository.save(comment);

    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .nickname(comment.getMember().getNickname())
            .content(comment.getContent())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> updateComment(Long id, Long commentId,  CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = postService.isPresentPost(id);
    if (post == null) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment comment = isPresentComment(commentId);
    if (comment == null) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

//    Member member = memberRepository.findById(1L).orElseGet(Member::new);

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    comment.update(requestDto);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .nickname(comment.getMember().getNickname())
            .content(comment.getContent())
//            .modifiedAt(comment.getModifiedAt())
//            .createdAt(comment.getCreatedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteComment(Long id , Long commentId, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = isPresentComment(commentId);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    commentRepository.delete(comment);
    return ResponseDto.success(commentId + " 번 댓글이 삭제되었습니다.");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }


//  @Transactional(readOnly = true)
//  public ResponseDto<?> getAllCommentsByPost(Long postId) {
//    Post post = postService.isPresentPost(postId);
//    if (post == null) {
//      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
//    }
//    List<Comment> commentList = commentRepository.findAllByPost(post);
//    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//    for (Comment comment : commentList) {
//      commentResponseDtoList.add(
//              CommentResponseDto.builder()
//                      .id(comment.getId())
//                      .author(comment.getMember().getNickname())
//                      .content(comment.getContent())
//                      .createdAt(comment.getCreatedAt())
//                      .modifiedAt(comment.getModifiedAt())
//                      .build()
//      );
//    }
//    return ResponseDto.success(commentResponseDtoList);
//  }
}
