package com.spring.security.dbauth;

import java.util.Optional;

public interface ApplicationUserDao {
    Optional<ApplicationUserDetails> selectApplicationUserByUsername(String username);
}
