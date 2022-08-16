package com.example.intermediate.service;

import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostListResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("400", "INVALID_TOKEN");
        }
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .imageUrl(requestDto.getImageUrl())
                .member(member)
                .build();
        postRepository.save(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .imageUrl(post.getImageUrl())
                        .modifiedAt(post.getModifiedAt())
                        .content(post.getContent())
                        .nickname(post.getMember().getNickname())
//            .createdAt(post.getCreatedAt())
                        .build()
        );
    }

    //게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseGet(null);
        if (null == post) {
            return ResponseDto.fail("400", "Not existing postId");
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .imageUrl(post.getImageUrl())
                        .modifiedAt(post.getModifiedAt())
                        .content(post.getContent())
                        .nickname(post.getMember().getNickname())
                        .build()
        );
    }

//    @Transactional(readOnly = true)
//    public ResponseDto<?> getPost(Long postId,int commentsNum, int pageLimit) {
//
////        Post post = isPresentPost(postId);
//        Post post = postRepository.findById(postId).orElseGet(null);
//        if (null == post) {
//            return ResponseDto.fail("400", "Not existing postId");
//        }
//
//        Pageable pageable = PageRequest.of(commentsNum, pageLimit );
//        List<Comment> commentList = commentRepository.findAllByPost(post, pageable);
//        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//        for (Comment comment : commentList) {
//            commentResponseDtoList.add(
//                    CommentResponseDto.builder()
//                            .id(comment.getId())
//                            .author(comment.getMember().getNickname())
//                            .content(comment.getContent())
//                            .createdAt(comment.getCreatedAt())
//                            .modifiedAt(comment.getModifiedAt())
//                            .build()
//            );
//        }
//
//        return ResponseDto.success(
//                PostResponseDto.builder()
//                        .postId(post.getPostId())
//                        .title(post.getTitle())
//                        .imageUrl(post.getImageUrl())
//                        .modifiedAt(post.getModifiedAt())
//                        .content(post.getContent())
//                        .nickname(post.getMember().getNickname())
//                        .comments(commentResponseDtoList)
////            .createdAt(post.getCreatedAt())
//                        .build()
//        );
//    }
    //게시글 상세조회 댓글 분리
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllCommentsByPostId(Long postId, int commentsNum, int pageLimit){
        Post post = isPresentPost(postId);
        if (post == null) {
            return ResponseDto.fail("400", "Not existing postId");
        }

        Pageable pageable = PageRequest.of(commentsNum, pageLimit );
        List<Comment> commentList = commentRepository.findAllByPost(post,pageable);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .id(comment.getId())
                            .nickname(comment.getMember().getNickname())
                            .content(comment.getContent())
                            .build()
            );
        }
        return ResponseDto.success(commentResponseDtoList);
    }

    //게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(int pageNum, int pageLimit) {
        Pageable pageable = PageRequest.of(pageNum, pageLimit );
        List<Post> allByOrderByModifiedAtDesc = postRepository.findAllByOrderByModifiedAtDesc(pageable);
        List<PostListResponseDto> dtoList = new ArrayList<>();

        for(Post post : allByOrderByModifiedAtDesc){
            Long postId = post.getPostId();
            PostListResponseDto postListResponseDto = new PostListResponseDto(post);
            dtoList.add(postListResponseDto);
        }
        return ResponseDto.success(dtoList);
    }

    @Transactional
    public ResponseDto<Post> updatePost(Long postId, PostRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("400", "INVALID_TOKEN");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("400", "Not existing postId");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("400", "Modified Author Only");
        }

        post.update(requestDto);
        return ResponseDto.success(post);
    }

    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("400", "INVALID_TOKEN");
        }

        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("400", "Not existing postId");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("400", "Deleted Author Only");
        }

        postRepository.delete(post);
        return ResponseDto.success("delete success");
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}
