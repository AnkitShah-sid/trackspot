package com.trackspot.entities.jwt;


import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResponseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String token;
}
