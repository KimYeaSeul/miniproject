package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.intermediate.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String nickname;
    //이미지 URl
//    private String imageUrl;
    //이미지 UrlList
    private List<String> imageUrl;
//    private List<CommentResponseDto> comments;
    //  private LocalDateTime createdAt;
    private int numComments;
    private LocalDateTime modifiedAt;
    private boolean IsMine;
}
