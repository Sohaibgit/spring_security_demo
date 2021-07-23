package com.spring.security.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.spring.security.security.ApplicationUserPermissions.*;
import static com.spring.security.security.ApplicationUserRoles.*;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSecurityConfig.class);

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSES_WRITE.getPermission())
                .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(COURSES_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(COURSES_WRITE.getPermission())
                .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name(), STUDENT.name())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails studentUser = User.builder()
                .username("student")
                .password(passwordEncoder.encode("student"))
//                .roles(STUDENT.name()) // ROLE_STUDENT
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
//                .roles(ADMIN.name()) // ROLE_ADMIN
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails tomUser = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMINTRAINEE.name()) // ROLE_ADMINTRAINEE
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                studentUser,
                adminUser,
                tomUser
        );
    }
}
