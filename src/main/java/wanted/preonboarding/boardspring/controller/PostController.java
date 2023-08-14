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
import wanted.preonboarding.boardspring.domain.dto.PostDefaultRequestDto;
import wanted.preonboarding.boardspring.domain.dto.PostDefaultResponseDto;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.domain.entity.Post;
import wanted.preonboarding.boardspring.auth.JwtProvider;
import wanted.preonboarding.boardspring.service.MemberService;
import wanted.preonboarding.boardspring.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;

import static wanted.preonboarding.boardspring.auth.SecurityConfig.*;

@Api(tags = "게시글")
@Validated
@RestController
@RequestMapping(API_ROOT_URL)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PostController {

    @Autowired
    public JwtProvider jwtProvider;
    @Autowired
    public MemberService memberService;
    @Autowired
    public PostService postService;

    @ApiOperation(value = "게시글 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "POST_SAVED",
                    content = @Content(schema = @Schema(implementation = PostDefaultResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "404",
                    description = "MEMBER_NOT_FOUND"),
            @ApiResponse(responseCode = "500",
                    description = "SERVER_ERROR"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/member/post")
    public ResponseEntity<DefaultResponseDto<Object>> savePost(
            HttpServletRequest servletRequest,
            @RequestBody @Valid PostDefaultRequestDto request
    ) {

        long memberId = Long.parseLong(jwtProvider.getUsername(servletRequest.getHeader(AUTHENTICATION_HEADER_NAME)));
        Member member = memberService.findById(memberId);

        Post post = postService.save(request.toEntity(member));
        PostDefaultResponseDto response = new PostDefaultResponseDto(post);

        return ResponseEntity.status(201)
                .body(DefaultResponseDto.builder()
                        .responseCode("POST_SAVED")
                        .responseMessage("게시글 등록 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "게시글 전체 조회", notes = "- 최신순 응답합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "POSTS_FOUND",
                    content = @Content(schema = @Schema(implementation = PostDefaultResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "500",
                    description = "SERVER_ERROR"),
    })
    @GetMapping("/v1/posts")
    public ResponseEntity<DefaultResponseDto<Object>> foundAllPost(
            HttpServletRequest servletRequest,
            @RequestParam int pageNumber,
            @RequestParam(required = false) Integer pageCount
    ) {
        List<PostDefaultResponseDto> responses = postService.findAll(pageNumber, pageCount);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("POSTS_FOUND")
                        .responseMessage("게시글 전체 조회 완료")
                        .data(responses)
                        .build());
    }

    @ApiOperation(value = "게시글 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "POST_FOUND",
                    content = @Content(schema = @Schema(implementation = PostDefaultResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "404",
                    description = "POST_NOT_FOUND"),
            @ApiResponse(responseCode = "500",
                    description = "SERVER_ERROR"),
    })
    @GetMapping("/v1/post/{postId}")
    public ResponseEntity<DefaultResponseDto<Object>> foundPost(
            HttpServletRequest servletRequest,
            @PathVariable(value = "postId")
            @Positive(message = "검색할 게시글 식별자는 양수만 가능합니다.")
            Long postId
    ) {

        PostDefaultResponseDto response = new PostDefaultResponseDto(postService.findById(postId));

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("POST_FOUND")
                        .responseMessage("게시글 단건 조회 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "게시글 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "POST_UPDATED",
                    content = @Content(schema = @Schema(implementation = PostDefaultResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "403",
                    description = "ACCESS_DENIED_POST"),
            @ApiResponse(responseCode = "404",
                    description = "POST_NOT_FOUND"),
            @ApiResponse(responseCode = "500",
                    description = "SERVER_ERROR"),
    })
    @PutMapping("/v1/member/post/{postId}")
    public ResponseEntity<DefaultResponseDto<Object>> updatePost(
            HttpServletRequest servletRequest,
            @PathVariable(value = "postId")
            @Positive(message = "검색할 게시글 식별자는 양수만 가능합니다.")
            Long postId,
            @RequestBody @Valid PostDefaultRequestDto request
    ) {

        long memberId = Long.parseLong(jwtProvider.getUsername(servletRequest.getHeader(AUTHENTICATION_HEADER_NAME)));
        Member member = memberService.findById(memberId);
        Post post = postService.findById(postId);

        postService.validatePostAuthorization(member, post);
        postService.update(post, request);
        PostDefaultResponseDto response = new PostDefaultResponseDto(post);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("POST_UPDATED")
                        .responseMessage("게시글 수정 완료")
                        .data(response)
                        .build());
    }

    @ApiOperation(value = "게시글 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "POST_DELETED",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401",
                    description = "UNAUTHORIZED_MEMBER"),
            @ApiResponse(responseCode = "403",
                    description = "ACCESS_DENIED_POST"),
            @ApiResponse(responseCode = "404",
                    description = "POST_NOT_FOUND"),
            @ApiResponse(responseCode = "500",
                    description = "SERVER_ERROR"),
    })
    @DeleteMapping("/v1/member/post/{postId}")
    public ResponseEntity<DefaultResponseDto<Object>> deletePost(
            HttpServletRequest servletRequest,
            @PathVariable(value = "postId")
            @Positive(message = "검색할 게시글 식별자는 양수만 가능합니다.")
            Long postId
    ) {

        long memberId = Long.parseLong(jwtProvider.getUsername(servletRequest.getHeader(AUTHENTICATION_HEADER_NAME)));
        Member member = memberService.findById(memberId);
        Post post = postService.findById(postId);

        postService.validatePostAuthorization(member, post);
        postService.delete(post);

        return ResponseEntity.status(200)
                .body(DefaultResponseDto.builder()
                        .responseCode("POST_DELETED")
                        .responseMessage("게시글 삭제 완료")
                        .build());
    }
}
