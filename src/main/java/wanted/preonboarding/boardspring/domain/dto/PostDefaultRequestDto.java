package wanted.preonboarding.boardspring.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wanted.preonboarding.boardspring.common.ValidationSequence;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.domain.entity.Post;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({PostDefaultRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.NotNull.class,
        ValidationSequence.Size.class,
})
@ApiModel(value = "Post 기본 요청")
public class PostDefaultRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "게시글 제목", example = "안뇽")
    @NotBlank(message = "제목 입력은 필수입니다.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 2, max = 15, message = "제목은 2-15자 여야 합니다.", groups = ValidationSequence.Size.class)
    private String title;

    @ApiModelProperty(position = 2, required = true, value = "게시글 내용", example = "게시글 내용입니다.")
    @NotBlank(message = "제목 입력은 필수입니다.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 5, max = 50, message = "내용은 5-50자 여야 합니다.", groups = ValidationSequence.Size.class)
    private String content;

    @ApiModelProperty(position = 3, required = true, value = "익명 여부", allowableValues = "true, false",
            example = "false")
    @NotNull(message = "익명 여부 입력은 필수입니다.", groups = ValidationSequence.NotNull.class)
    private Boolean isAnonymous;

    public Post toEntity(Member member) {
        return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .isAnonymous(this.isAnonymous)
                    .member(member)
                    .build();
    }
}
