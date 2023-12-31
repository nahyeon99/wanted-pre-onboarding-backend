package wanted.preonboarding.boardspring.auth;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import wanted.preonboarding.boardspring.exception.CustomException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;

import static wanted.preonboarding.boardspring.auth.SecurityConfig.AUTHENTICATION_HEADER_NAME;
import static wanted.preonboarding.boardspring.exception.ExceptionCode.*;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;
    private final long ACCESS_TOKEN_VALID_TIME = 1000 * 60 * 60 * 2L;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public String createToken(String memberId, Collection<String> authorities) {
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", authorities);
        Date now = new Date();

        String createdToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
        return createdToken;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(JWT_SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (IllegalArgumentException | JwtException e) {
            throw new CustomException(e, UNAUTHORIZED_MEMBER);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHENTICATION_HEADER_NAME);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
