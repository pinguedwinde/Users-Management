package org.sliga.usersmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder(toBuilder = true, builderClassName = "Builder")
@JsonIgnoreProperties(value={ "password" }, allowSetters= true)
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
    @JsonIgnoreProperties("password")
    private String password;
    private String email;
    private String profileImageUrl;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private boolean isEnabled;
    private boolean isNonLocked;
    private String role;

    @ElementCollection
    @CollectionTable(name = "user_authorities", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "authorities")
    private List<String> authorities = new ArrayList<>();

}
