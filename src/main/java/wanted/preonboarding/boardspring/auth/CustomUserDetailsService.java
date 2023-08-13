package wanted.preonboarding.boardspring.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wanted.preonboarding.boardspring.domain.entity.Member;
import wanted.preonboarding.boardspring.repository.MemberRepository;


@Service("UserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member loadUserByUsername(String username) throws UsernameNotFoundException {
        Member foundMember = memberRepository.findById(Long.parseLong(username))
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
                });

        if (foundMember.getIsDeleted())
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");

        return foundMember;
    }
}
