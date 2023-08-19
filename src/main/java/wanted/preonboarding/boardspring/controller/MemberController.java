package wanted.preonboarding.boardspring.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wanted.preonboarding.boardspring.common.DefaultResponseDto;
import wanted.preonboarding.boardspring.domain.dto.MemberDefaultRequestDto;
import wanted.preonboarding.boardspring.domain.dto.MemberDefaultResponseDto;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.auth.JwtProvider;
import wanted.preonboarding.boardspring.service.MemberService;

import javax.validation.Valid;

import static wanted.preonboarding.boardspring.auth.SecurityConfig.AUTHENTICATION_HEADER_NAME;
import static wanted.preonboarding.boardspring.auth.SecurityConfig.AUTHENTICATION_URL;

@Api(tags = "회원")
@Validated
@RestController
@RequestMapping(AUTHENTICATION_URL)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberController {

    @Autowired
    public JwtProvider jwtProvider;
    @Autowired
    public MemberService memberService;


    @ApiOperation(value = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "MEMBER_REGISTERED",
                    content = @Content(schema = @Schema(implementation = MemberDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "- FIELD_REQUIRED\n " +
                            "- EMAIL_CHARACTER_INVALID\n" +
                            "- PASSWORD_LENGTH_INVALID"),
            @ApiResponse(responseCode = "409",
                    description = "- DUPLICATE_EMAIL"),
            @ApiResponse(responseCode = "500",
                    description = "- SERVER_ERROR"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/register")
    public ResponseEntity<DefaultResponseDto<Object>> register(
            @RequestBody @Valid MemberDefaultRequestDto request
    ) {

        memberService.validateEmailExistence(request.getEmail());
        Member member = memberService.create(request);
        MemberDefaultResponseDto response = new MemberDefaultResponseDto(member);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("MEMBER_REGISTERED")
                        .responseMessage("회원 등록 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "로그인")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "MEMBER_LOGIN",
                    content = @Content(schema = @Schema(implementation = MemberDefaultResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "- FIELD_REQUIRED\n " +
                            "- EMAIL_CHARACTER_INVALID\n" +
                            "- PASSWORD_LENGTH_INVALID"),
            @ApiResponse(responseCode = "401",
                    description = "- UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "404",
                    description = "- EMAIL_NOT_FOUND"),
            @ApiResponse(responseCode = "500",
                    description = "- SERVER_ERROR"),
    })
    @PostMapping("/v1/login")
    public ResponseEntity<DefaultResponseDto<Object>> login(
            @RequestBody @Valid MemberDefaultRequestDto request
    ) {

        Member member = memberService.findByEmail(request.getEmail());
        memberService.validatePassword(member, request.getPassword());

        String token = jwtProvider.createToken(String.valueOf(member.getId()), member.getRoles());
        MemberDefaultResponseDto response = new MemberDefaultResponseDto(member);

        return ResponseEntity.status(200)
                .header(AUTHENTICATION_HEADER_NAME, token)
                .body(DefaultResponseDto.builder()
                        .responseCode("MEMBER_LOGIN")
                        .responseMessage("회원 로그인 완료")
                        .data(response)
                        .build());
    }
}
