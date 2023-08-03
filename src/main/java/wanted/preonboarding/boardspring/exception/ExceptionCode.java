package wanted.preonboarding.boardspring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /**
     * 400 BAD_REQUEST : 잘못된 요청
     */
    FIELD_REQUIRED(BAD_REQUEST, "입력은 필수 입니다."),

    // 형식
    EMAIL_CHARACTER_INVALID(BAD_REQUEST, "올바른 형식의 이메일이 아닙니다."),

    // 길이
    PASSWORD_LENGTH_INVALID(BAD_REQUEST, "비밀번호는 8자 이상 여야 합니다."),
    TITLE_LENGTH_INVALID(BAD_REQUEST, "게시글 제목은 1~30자 이내 여야 합니다."),
    CONTENT_LENGTH_INVALID(BAD_REQUEST, "게시글 혹은 댓글 내용은 1~50자 이내 여야 합니다."),

    /**
     * 401 UNAUTHORIZED : 인증되지 않은 사용자
     */
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "로그인이 필요합니다."),
    UNAUTHORIZED_DELETED_MEMBER(UNAUTHORIZED, "탈퇴한 회원입니다. 로그아웃이 필요합니다."),
    UNAUTHORIZED_PASSWORD(UNAUTHORIZED, "올바르지 않은 비밀번호 입니다."),

    /**
     * 403 FORBIDDEN : 권한이 없는 사용자
     */
    ACCESSDENIED_RESOURCE(FORBIDDEN, "접근 권한이 없습니다."),
    ACCESSDENIED_POST(FORBIDDEN, "게시글 수정 및 삭제 권한이 없습니다."),

    /**
     * 404 NOT_FOUND : Resource 를 찾을 수 없음
     */
    EMAIL_NOT_FOUND(NOT_FOUND, "등록된 이메일이 없습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "등록된 멤버가 없습니다."),
    POST_NOT_FOUND(NOT_FOUND, "등록된 게시글이 없습니다."),

    DELETED_POST(NOT_FOUND, "삭제된 게시글 입니다."),
    DELETED_COMMENT(NOT_FOUND, "삭제된 댓글 입니다."),

    /**
     * 405 METHOD_NOT_ALLOWED : 대상 리소스가 해당 메서드를 지원하지 않음
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메서드입니다."),

    /**
     * 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재
     */
    DUPLICATE_EMAIL(CONFLICT, "이미 등록된 이메일 입니다."),
    DUPLICATE_MEMBER(CONFLICT, "이미 등록된 멤버 입니다."),

    /**
     * 500 SERVER_ERROR : 서버 에러
     */
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
