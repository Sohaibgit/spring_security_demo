package com.spring.security.security;

import com.google.common.collect.Sets;

import java.util.Set;

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

    @Override
    public String toString() {
        return "ApplicationUserRoles{" +
                "permissions=" + permissions +
                '}';
    }
}
