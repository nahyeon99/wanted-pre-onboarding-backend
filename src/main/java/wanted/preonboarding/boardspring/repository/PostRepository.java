package wanted.preonboarding.boardspring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import wanted.preonboarding.boardspring.domain.entity.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndIsDeletedIsFalse(Long id);

    Page<Post> findByIsDeletedIsFalse(Pageable pageable);
}
