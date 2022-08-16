package com.example.intermediate.controller.response;

import com.example.intermediate.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostListResponseDto {
    private Long id;
    private String title;
    private String nickname;
//    private String imageUrl;
    private List<String> imageUrl;
    private String content;
    //    private long postHeartCount;
    private int numComments;
    //    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PostListResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.nickname = post.getMember().getNickname();
        this.imageUrl = post.getImageUrl();
        this.content = post.getContent();
//        this.postHeartCount = postHeartCount;
        this.numComments = post.getComments().size();
//        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
    }
}
