package tacos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.server.ApacheDSContainer;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/design", "/orders")
                .access("hasRole('ROLE_USER')")
                .antMatchers("/", "/**").access("permitAll")
                .and()
                .httpBasic();
        return http.build();
    }

    /* 인메모리 사용자 스토어
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}password1")
                .authorities("ROLE_USER")
                .and()
                .withUser("user2")
                .password("{noop}password2")
                .authorities("ROLE_USER");
    } */

    /* JDBC 사용자 스토어
    @Autowired
    DataSource dataSource;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select username, password, enabled from users " +
                                "where username=?")
                .authoritiesByUsernameQuery(
                        "select username, authority from authorities " +
                                "where username=?")
                .passwordEncoder(new NoEncodingPasswordEncoder());
    } */

    /* LDAP 사용자 스토어
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
                .userSearchBase("ou=people")
                .userSearchFilter("(uid={0})")
                .groupSearchBase("ou=groups")
                .groupSearchFilter("member={0}")
                .contextSource()
                .root("dc=tacocloud,dc=com")
                .ldif("classpath:users.ldif")
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPasscode");
    } */
}
