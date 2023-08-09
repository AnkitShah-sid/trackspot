package com.trackspot.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Arrays;
import java.util.Collection;

/**
 * Admin entity.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Admin extends Audit implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @Column(unique = true, nullable = false)
    @Email
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    @Override
    public void setCreatedBy(String createdBy) {
        super.setCreatedBy(createdBy);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return the authorities/roles for the user (e.g., ROLE_ADMIN, ROLE_USER)
        // You can return a list of SimpleGrantedAuthority objects representing the roles of the user
        // For example:
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
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
        return true;
    }
}


/*

//Add column
ALTER TABLE admin
ADD email VARCHAR(255);

ALTER TABLE admin
ADD firstName VARCHAR(255);

ALTER TABLE admin
ADD lastname VARCHAR(255);

// Delete a column
ALTER TABLE admin
DROP COLUMN username;


insert data
INSERT INTO admin (id, email, password, first_name, last_name) VALUES (1, 'user@example.com', 'password', 'ankit', 'shah');
INSERT INTO admin (id, email, password, first_name, last_name, created_by ) VALUES (1, 'example@example.com', 'password123', 'ankit', 'shah', 'ankitShah');

*/