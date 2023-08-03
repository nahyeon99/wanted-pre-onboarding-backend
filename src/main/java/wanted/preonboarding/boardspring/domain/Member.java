package wanted.preonboarding.boardspring.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String email, String password) {
        this.email = email;
        this.password = password;

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
}
