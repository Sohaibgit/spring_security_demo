package com.spring.security.dbauth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.spring.security.security.ApplicationUserRoles.STUDENT;

@Repository("fake")
public class ApplicationUserDaoImpl implements ApplicationUserDao{

    private PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationUserDaoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUserDetails> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUserDetails -> username.equalsIgnoreCase(applicationUserDetails.getUsername()))
                .findFirst();
    }

    private List<ApplicationUserDetails> getApplicationUsers(){
        List<ApplicationUserDetails> applicationUsers = Lists.newArrayList(
                new ApplicationUserDetails(
                        STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("student"),
                        "student",
                        true,
                        true,
                        true,
                        true
                        ),

                new ApplicationUserDetails(
                        STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("admin"),
                        "admin",
                        true,
                        true,
                        true,
                        true
                ),

                new ApplicationUserDetails(
                        STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("admintrainee"),
                        "admintrainee",
                        true,
                        true,
                        true,
                        true
                )
        );

        return applicationUsers;
    }
}
