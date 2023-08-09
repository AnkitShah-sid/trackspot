package com.trackspot.entities.jwt;


import com.trackspot.entities.Admin;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public class JwtRequestModel extends Admin implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2636936156391265891L;

    public JwtRequestModel(String email_, String password_) {
        super();
        this.email = email_; password = password_;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
