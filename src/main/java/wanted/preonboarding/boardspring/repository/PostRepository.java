package wanted.preonboarding.boardspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.preonboarding.boardspring.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
