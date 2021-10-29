package com.ikub.reservationapp.security;

import com.ikub.reservationapp.security.ldap.service.LdapAuthenticationService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header.string}")
    public String HEADER_STRING;

    @Value("${jwt.token.prefix}")
    public String TOKEN_PREFIX;

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenProvider tokenProvider;
    @Resource(name = "ldapAuthService")
    private LdapAuthenticationService ldapAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = tokenProvider.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("An error occurred while fetching Username from Token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token has expired", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("Couldn't find bearer string, header will be ignored");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetailsFromLdap = ldapAuthenticationService.loadUserDetailsFromLdap(username);

            if (userDetailsFromLdap != null)
                validateTokenAndSetAuthentication(authToken, userDetailsFromLdap, request);
            else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                validateTokenAndSetAuthentication(authToken, userDetails, request);
            }
        }
        chain.doFilter(request, response);
    }

    protected void validateTokenAndSetAuthentication(String token, UserDetails userDetails, HttpServletRequest request) {
        if (tokenProvider.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthenticationToken(token, SecurityContextHolder.getContext().getAuthentication(), userDetails);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            logger.info("Authenticated user " + userDetails.getUsername() + ", setting security context");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}