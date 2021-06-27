package org.sliga.usersmanagement.security.utils;

import static org.sliga.usersmanagement.security.utils.Authority.*;

public enum Role {

    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_USER(SUPER_USER_AUTHORITIES);

    public final String[]  authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
