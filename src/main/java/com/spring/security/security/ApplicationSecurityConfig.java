package com.spring.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails mariumUser = User.builder()
                .username("student")
                .password("student")
                .roles("STUDENT")
                .build();

        return new InMemoryUserDetailsManager(mariumUser);
    }
}
