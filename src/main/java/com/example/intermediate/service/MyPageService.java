package com.example.intermediate.service;

import com.example.intermediate.controller.response.MyPageResponseDto;
import com.example.intermediate.controller.response.PostListResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    public ResponseDto<?> showMypage(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("400",
                    "Login is required.");
        }
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return ResponseDto.fail("400",
                    "Token failed");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("400", "INVALID_TOKEN");
        }
        List<Post> posts = postRepository.findAllByMemberOrderByModifiedAtDesc(member);
        List<Comment> Comments = commentRepository.findAllByMember(member);
        List<PostListResponseDto> dtoList = new ArrayList<>();
        for(Post post : posts){
            PostListResponseDto postListResponseDto = new PostListResponseDto(post);
            dtoList.add(postListResponseDto);
        }
        return ResponseDto.success(
                MyPageResponseDto.builder()
                        .numPosts((long) posts.size())
                        .numComments((long) Comments.size())
                        .posts(dtoList)
                        .build()
        );

    }
}
