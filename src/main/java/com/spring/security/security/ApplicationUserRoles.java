package com.spring.security.security;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.spring.security.security.ApplicationUserPermissions.*;

public enum ApplicationUserRoles {
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(STUDENT_READ, STUDENT_WRITE, COURSES_READ, COURSES_WRITE)),
    ADMINTRAINEE(Sets.newHashSet(STUDENT_READ, COURSES_READ));

    private Set<ApplicationUserPermissions> permissions;

    ApplicationUserRoles(Set<ApplicationUserPermissions> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermissions> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return permissions;
    }

    @Override
    public String toString() {
        return "ApplicationUserRoles{" +
                "permissions=" + permissions +
                '}';
    }
}
