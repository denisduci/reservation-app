package com.ikub.reservationapp.security;

import com.ikub.reservationapp.security.database.DbAuthenticationProvider;
import com.ikub.reservationapp.security.ldap.LdapAuthenticationProvider;
import com.ikub.reservationapp.security.ldap.encoder.LdapCustomPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;
    @Resource(name = "ldapAuthenticationProvider")
    private LdapAuthenticationProvider ldapAuthenticationProvider;
    @Autowired
    private DbAuthenticationProvider dbAuthenticationProvider;
    @Autowired
    private UnauthorizedEntryPoint unauthorizedEntryPoint;
    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.ldapAuthentication()
//                .userDnPatterns("uid={0},ou=people")
//                //.groupSearchBase("ou=groups")
//               .contextSource().url("ldap://localhost:8389/dc=springframework,dc=org").and()
//                .passwordCompare().passwordEncoder(encoder())
//                .passwordAttribute("userPassword");

        auth.authenticationProvider(ldapAuthenticationProvider)
                .ldapAuthentication().userDnPatterns("uid={0},ou=people")
                .contextSource(contextSource()).passwordCompare().passwordEncoder(passwordEncoder()).passwordAttribute("userPassword");
        auth.authenticationProvider(dbAuthenticationProvider).userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:8389");
        //contextSource.setAnonymousReadOnly(true);
        //contextSource.setBase("ou=groups");
        contextSource.setBase("dc=springframework,dc=org");
        contextSource.setUserDn("uid=admin,ou=people,dc=springframework,dc=org");
        contextSource.setPassword("{SHA}JuZjIoqmb7oTqegY1eP5W+EF9GU=");
        contextSource.afterPropertiesSet();
        return contextSource;
    }

//    @Bean
//    public PasswordEncoder encoderGlobal() {
//        String encodingId = "bcrypt";
//        Map<String, PasswordEncoder> encoders = new HashMap<>();
//        encoders.put(encodingId, new BCryptPasswordEncoder());
//        encoders.put("custom", new LdapCustomPasswordEncoder());
//        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(encodingId,encoders);
//        //delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
//        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new LdapCustomPasswordEncoder());
//        return delegatingPasswordEncoder;
//    }

    @Bean
    public SpringSecurityLdapTemplate ldapTemplate() {
        return new SpringSecurityLdapTemplate(contextSource());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/users/authenticate", "/users/register", "/users/refreshtoken").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Bean(value = "bcrypt")
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(value = "ldap")
    public LdapCustomPasswordEncoder passwordEncoder() {
        return new LdapCustomPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}