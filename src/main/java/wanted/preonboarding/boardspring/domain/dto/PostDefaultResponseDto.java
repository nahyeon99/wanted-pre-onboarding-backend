package wanted.preonboarding.boardspring.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import wanted.preonboarding.boardspring.domain.entity.Post;

@Data
public class PostDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자", example = "1")
    private Long postId;

    @ApiModelProperty(position = 2, required = true, value = "작성한 회원 식별자", example = "1")
    private Long memberId;

    @ApiModelProperty(position = 3, required = true, value = "게시글 제목", example = "안뇽")
    private String title;

    @ApiModelProperty(position = 4, required = true, value = "게시글 내용", example = "게시글 내용입니다.")
    private String content;

    @ApiModelProperty(position = 5, required = true, value = "익명 여부", example = "false")
    private Boolean isAnonymous;

    public PostDefaultResponseDto(Post post) {
        this.postId = post.getId();
        this.memberId = post.getMember().getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.isAnonymous = post.getIsAnonymous();
    }
}
