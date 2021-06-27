package org.sliga.usersmanagement.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static org.sliga.usersmanagement.utils.SecurityConstants.*;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withIssuedAt(new Date())
                .withAudience(TOKEN_AUDIENCE)
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .sign(HMAC256(this.secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        return Arrays.stream(getClaimsFromToken(token))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username,
                                            List<GrantedAuthority> authorities,
                                            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPasswordAuthToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(jwtVerifier, token);
    }

    public String getSubjectFromToken(String token) {
        return getJWTVerifier().verify(token).getSubject();
    }

    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
        return jwtVerifier.verify(token)
                .getExpiresAt()
                .before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return jwtVerifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier jwtVerifier;
        try {
            jwtVerifier = JWT.require(Algorithm.HMAC256(this.secret))
                    .withIssuer(TOKEN_ISSUER)
                    .build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
                .toArray(String[]::new);
    }
}
