package org.sliga.usersmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
}
