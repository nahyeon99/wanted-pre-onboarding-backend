package wanted.preonboarding.boardspring.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wanted.preonboarding.boardspring.common.ValidationSequence;
import wanted.preonboarding.boardspring.domain.entity.Member;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@GroupSequence({MemberDefaultRequestDto.class,
        ValidationSequence.NotBlank.class,
        ValidationSequence.Email.class,
        ValidationSequence.Size.class,
})
@ApiModel(value = "Member 기본 요청")
public class MemberDefaultRequestDto {

    @ApiModelProperty(position = 1, required = true, value = "이메일", example = "test@wanted.com")
    @NotBlank(message = "이메일 입력은 필수입니다.", groups = ValidationSequence.NotBlank.class)
    @Email(message = "이메일 형식이 올바르지 않습니다.", groups = ValidationSequence.Email.class)
    private String email;

    @ApiModelProperty(position = 2, required = true, value = "비밀번호", example = "test2023!")
    @NotBlank(message = "비밀번호 입력은 필수입니다.", groups = ValidationSequence.NotBlank.class)
    @Size(min = 8, message = "비밀번호는 8~자 여야 합니다.", groups = ValidationSequence.Size.class)
    private String password;

    public Member toEntity(String encodedPassword) {
    return Member.builder()
            .email(this.email)
            .password(encodedPassword)
            .build();
}
}
