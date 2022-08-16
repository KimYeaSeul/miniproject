package com.example.intermediate.domain;

import com.example.intermediate.controller.request.PostRequestDto;

import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    //이미지 URL
//    @Column(nullable = false)
//    private String imageUrl;
    @Column(name="imageUrl", nullable = false)
    @ElementCollection(targetClass = String.class)
    private List<String> imageUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(PostRequestDto postRequestDto) {
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.imageUrl = postRequestDto.getImageUrl();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

}
