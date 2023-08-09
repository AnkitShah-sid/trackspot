package com.trackspot.services;

import com.trackspot.entities.UpdateUserStatus;
import com.trackspot.entities.User;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;

import java.util.List;
import java.util.Optional;


public interface UserService {

    Iterable<User> listAllUser();

    User getUserById(Integer id);

    User saveUser(User user) throws Exception;

    User updateUser(Long userId, User user) throws Exception;

    void deleteUser(Long id) throws Exception;

    LoginResponseModel createToken(JwtRequestModel request) throws Exception;

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAll();

    List<User> getAllUsers();

    boolean updateStatus(UpdateUserStatus updateUserStatus) throws Exception;

    User getUser(long userId);

    User setPassword(Long id, String password) throws Exception;
}
