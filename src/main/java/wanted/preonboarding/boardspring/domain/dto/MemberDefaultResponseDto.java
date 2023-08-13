package wanted.preonboarding.boardspring.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import wanted.preonboarding.boardspring.domain.entity.Member;


@Data
public class MemberDefaultResponseDto {

    @ApiModelProperty(position = 1, required = true, value = "회원 식별자", example = "1")
    private Long memberId;

    @ApiModelProperty(position = 2, required = true, value = "이메일", example = "test@wanted.com")
    private String email;

    public MemberDefaultResponseDto(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
    }
}
