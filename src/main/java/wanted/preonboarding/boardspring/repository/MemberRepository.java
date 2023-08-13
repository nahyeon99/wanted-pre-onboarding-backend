package wanted.preonboarding.boardspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.preonboarding.boardspring.domain.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndIsDeletedIsFalse(Long id);
    Optional<Member> findByEmailAndIsDeletedIsFalse(String email);
}
