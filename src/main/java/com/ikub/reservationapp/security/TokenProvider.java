package com.ikub.reservationapp.security;

import com.ikub.reservationapp.common.model.AuthToken;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider implements Serializable {

    @Value("${jwt.token.validity}")
    public long TOKEN_VALIDITY;

    @Value("${jwt.token.refresh.validity}")
    public long REFRESH_TOKEN_VALIDITY;

    @Value("${jwt.signing.key}")
    public String SIGNING_KEY;

    @Value("${jwt.authorities.key}")
    public String AUTHORITIES_KEY;

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Autowired
    @Resource(name = "userService")
    private UserDetailsService userService;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        log.info("Is token expired? -> {}", expiration.before(new Date()));
        return expiration.before(new Date());
    }

    /**
     * @param authentication
     * @return AuthToken object containing access token and refresh token
     */
    public AuthToken generateTokenWithAuthentication(Authentication authentication) {
        log.info("Generating token with authentication...");
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        log.info("User authorities -> {}", authorities);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
        log.info("Generated access token is -> {} and refresh token -> {}", accessToken, refreshToken);
        return new AuthToken(accessToken, refreshToken);
    }

    /**
     *
     * @param userDetails to retrieve user info for token generation
     * @return AuthToken containing access token
     */
    public AuthToken generateTokenWithUserDetails(UserDetails userDetails) {
        log.info("Generating token with User Details...");
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("User authorities -> {}", authorities);
        String accessToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .compact();
        log.info("Generated access token is -> {}", accessToken);
        return new AuthToken(accessToken, "");
    }

    /**
     *
     * @param request
     * @return AuthToken new token after validating the refresh token
     */
    public AuthToken generateRefreshToken(HttpServletRequest request) {
        //String header = request.getHeader(HEADER_STRING);
        String header = request.getHeader("refresh-token");
        String username = null;
        String refreshToken = null;
        AuthToken authToken = new AuthToken();
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            refreshToken = header.replace(TOKEN_PREFIX, "");
            log.info("Refresh token token is -> {}", username);
            try {
                username = getUsernameFromToken(refreshToken);
            } catch (IllegalArgumentException e) {
                log.error("An error occurred while fetching Username from Token", e);
            } catch (ExpiredJwtException e) {
                log.warn("The token has expired", e);
            } catch (SignatureException e) {
                log.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            log.warn("Couldn't find bearer string, header will be ignored");
        }
        if (username != null /*&& SecurityContextHolder.getContext().getAuthentication() == null*/) {
            log.info("Username from refresh token is -> {}", username);
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (validateToken(refreshToken, userDetails)) {
                String accessToken = generateTokenWithUserDetails(userDetails).getAccessToken();
                authToken.setAccessToken(accessToken);
                authToken.setRefreshToken(refreshToken);
            }
        }
        return authToken;
    }

    /**
     * @param token
     * @param userDetails
     * @return true if username in token matches the UserDetails username and
     * if token is not expired
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        log.info("Username from User Details is -> {}", username);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final Authentication existingAuth, final UserDetails userDetails) {
        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
        final Claims claims = claimsJws.getBody();

        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}