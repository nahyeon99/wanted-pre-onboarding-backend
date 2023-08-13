package wanted.preonboarding.boardspring.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.preonboarding.boardspring.domain.dto.MemberDefaultRequestDto;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.exception.CustomException;
import wanted.preonboarding.boardspring.repository.MemberRepository;

import java.util.Optional;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberService {

    @Autowired
    public MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * DB에 회원 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Member save(Member member) {
        try {
            return memberRepository.save(member);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Member member) {
        try {
            member.delete();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 비밀번호 수정 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void updatePassword(Member member, String encodedPassword) {

        try {
            member.updatePassword(encodedPassword);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 회원 등록 |
     */
    @Transactional
    public Member create(MemberDefaultRequestDto request) {
        Member createdMember = request.toEntity(passwordEncoder.encode(request.getPassword()));
        return this.save(createdMember);
    }

    /**
     * 회원 단건 조회 |
     * 404(MEMBER_NOT_FOUND)
     */
    public Member findById(Long memberId) {
        return memberRepository.findByIdAndIsDeletedIsFalse(memberId)
                .orElseThrow(() -> new CustomException(null, MEMBER_NOT_FOUND));
    }

    /**
     * 이메일로 회원 단건 조회 |
     * 404(EMAIL_NOT_FOUND)
     */
    public Member findByEmail(String email) {

        return memberRepository.findByEmailAndIsDeletedIsFalse(email)
                .orElseThrow(() -> new CustomException(null, EMAIL_NOT_FOUND));
    }

    /**
     * 가입한 이메일 존재 여부 검증 |
     * 409(DUPLICATE_EMAIL)
     * 500(SERVER_ERROR)
     */
    public void validateEmailExistence(String email) {
        Optional<Member> mem = null;

        try {
            mem = memberRepository.findByEmailAndIsDeletedIsFalse(email);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }

        if (mem.isPresent())
            throw new CustomException(null, DUPLICATE_EMAIL);
    }

    /**
     * 비밀번호 일치 여부 검증 |
     * 401(UNAUTHORIZED_PASSWORD)
     */
    public void validatePassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(null, UNAUTHORIZED_PASSWORD);
        }
    }
}
