package com.medicapp.server.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String recoverCode;
    private String sex;

    @Column(
            name="enabled",
            nullable = false,
            columnDefinition = "BOOLEAN DEFAULT FALSE"
    )
    private Boolean enabled;

    @Column(
            name="profile_key",
            nullable = true,
            columnDefinition = "TEXT"
    )
    private String profile_key;

    @Column(
            name="profile_url",
            nullable = true,
            columnDefinition = "TEXT"
    )
    private String profile_url;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static Role getRoleFromString(String roleString) {
        switch(roleString) {
            case "ROLE_USER":
                return Role.ROLE_USER;
            case "ROLE_DOCTOR":
                return Role.ROLE_DOCTOR;
            case "ROLE_MODERATOR":
                return Role.ROLE_MODERATOR;
            default:
                return null;
        }
    }
}
