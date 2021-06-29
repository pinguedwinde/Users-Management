package org.sliga.usersmanagement.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.sliga.usersmanagement.utils.Authority.*;

public enum Role {

    ROLE_USER("USER", USER_AUTHORITIES),
    ROLE_HR("HR", HR_AUTHORITIES),
    ROLE_MANAGER("MANAGER", MANAGER_AUTHORITIES),
    ROLE_ADMIN("ADMIN", ADMIN_AUTHORITIES),
    ROLE_SUPER_USER("SUPER_ADMIN", SUPER_ADMIN_AUTHORITIES),
    UNDEFINED("UNDEFINED");

    public final String[]  authorities;

    Role(String label, String... authorities) {
        this.label = label;
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public final String label;
    private static final Map<String, Role> BY_LABEL = new HashMap<>();

    static {
        for(Role role : values()){
            BY_LABEL.put(role.label, role);
        }
    }


    public static Role getRoleByString(String label){
        label = label.toUpperCase(Locale.ROOT).trim();
        Role role = BY_LABEL.get(label);
        return role != null ? role : Role.UNDEFINED;
    }
}
