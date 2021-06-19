package org.sliga.usersmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private boolean isEnabled;
    private boolean isNonLocked;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "roles")
    private List<String> roles = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "authorities")
    private List<String> authorities = new ArrayList<>();
}
