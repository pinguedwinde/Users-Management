package org.sliga.usersmanagement.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserForm {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String username;
    private String currentUsername;
    @NonNull
    private String password;
    @NonNull
    private String email;
    private String profileImageUrl;
    private boolean isEnabled;
    private boolean isNonLocked;
    private String role;
}
