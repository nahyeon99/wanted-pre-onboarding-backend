package wanted.preonboarding.boardspring.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.preonboarding.boardspring.domain.dto.PostDefaultRequestDto;
import wanted.preonboarding.boardspring.domain.dto.PostDefaultResponseDto;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.domain.entity.Post;
import wanted.preonboarding.boardspring.exception.CustomException;
import wanted.preonboarding.boardspring.repository.PostRepository;


import java.util.List;
import java.util.stream.Collectors;

import static wanted.preonboarding.boardspring.exception.ExceptionCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PostService {

    @Autowired
    public PostRepository postRepository;

    /**
     * DB에 게시글 저장 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public Post save(Post post) {
        try {
            return postRepository.save(post);
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 게시글 수정 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void update(Post post, PostDefaultRequestDto request) {
        try {
            post.updatePost(request.getTitle(), request.getContent(), request.getIsAnonymous());
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 게시글 삭제 |
     * 500(SERVER_ERROR)
     */
    @Transactional
    public void delete(Post post) {
        try {
            post.delete();
        } catch (RuntimeException e) {
            throw new CustomException(e, SERVER_ERROR);
        }
    }

    /**
     * 게시글 단건 조회 |
     * 404(POST_NOT_FOUND)
     */
    public Post findById(long postId) {
        return postRepository.findByIdAndIsDeletedIsFalse(postId)
                .orElseThrow(() -> {
                    throw new CustomException(null, POST_NOT_FOUND);
                });
    }

    /**
     * 게시글 전체 조회 |
     * 500(SERVER_ERROR)
     */
    public List<PostDefaultResponseDto> findAll(int pageNumber, Integer pageCount) {

        if (pageCount == null)
            pageCount = 5;
        return postRepository.findByIsDeletedIsFalse(PageRequest.of(pageNumber,
                pageCount,
                Sort.Direction.DESC,
                "id"))
                .stream().collect(Collectors.toList())
                .stream().map(PostDefaultResponseDto::new).collect(Collectors.toList());
    }

    /**
     * 게시글 접근 권한 여부 검증 |
     * 403(ACCESS_DENIED_POST)
     */
    public void validatePostAuthorization(Member member, Post post) {
        
        if (!post.getMember().equals(member))
            throw new CustomException(null, ACCESS_DENIED_POST);
    }
}
