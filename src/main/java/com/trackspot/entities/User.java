package com.trackspot.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trackspot.entities.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends Audit implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    protected String email;

    protected String password;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    @Column(nullable = false)
    protected String telephone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ENABLED;

    @Transient
    protected EmulatorCount emulatorCount;

    @OneToMany(mappedBy="user")
    @JsonBackReference
    private List<EmulatorDetails> emulators = new ArrayList<>();

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @JsonProperty("authorities")
    private void deserializeAuthorities(List<Map<String, String>> authorityList) {
        authorities = authorityList.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.get("authority")))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Return true if the user account is not expired
        // You can implement any expiration logic based on your requirements
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Return true if the user account is not locked
        // You can implement any locking logic based on your requirements
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Return true if the user's credentials (password) are not expired
        // You can implement any credential expiration logic based on your requirements
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Return true if the user account is enabled
        // You can implement any account enabling/disabling logic based on your requirements
        return status == UserStatus.ENABLED;
    }
}

