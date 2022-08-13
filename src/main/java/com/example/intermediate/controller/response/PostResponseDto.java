package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String author;
    //이미지 URl
    private String imageUrl;
    private List<CommentResponseDto> commentResponseDtoList;
    //  private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
