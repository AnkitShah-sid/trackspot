package com.trackspot.services;


import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;

public interface ClientService {
    Object getCurrentClient() throws Exception;
    LoginResponseModel createToken(JwtRequestModel request) throws Exception;
}
