package wanted.preonboarding.boardspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.preonboarding.boardspring.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
