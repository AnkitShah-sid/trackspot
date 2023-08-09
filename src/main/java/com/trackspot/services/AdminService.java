package com.trackspot.services;


import com.trackspot.entities.Admin;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;

public interface AdminService {

    Iterable<Admin> listAllAdmin();

    Admin getAdminById(Integer id);

    Admin saveAdmin(Admin admin);

    void deleteAdmin(Integer id);

    LoginResponseModel createToken(JwtRequestModel request) throws Exception;

    Admin getCurrentAdmin() throws Exception;

    Admin setPassword(Integer id, String password) throws Exception;
}
