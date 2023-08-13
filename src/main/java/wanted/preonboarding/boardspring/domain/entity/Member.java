package wanted.preonboarding.boardspring.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wanted.preonboarding.boardspring.common.BaseEntity;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String email, String password) {
        this.email = email;
        this.password = password;

        this.roles.add("ROLE_USER");
        this.setIsDeleted(false);
    }

    public String getEmail() {
        return email;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public List<Post> getPosts() {
        List<Post> postList = this.posts;
        postList.removeIf(post -> post.getIsDeleted().equals(true));
        return postList;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void delete() {
        this.setIsDeleted(true);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !getIsDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !getIsDeleted();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !getIsDeleted();
    }

    @Override
    public boolean isEnabled() {
        return !getIsDeleted();
    }
}
