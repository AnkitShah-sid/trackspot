package com.trackspot.services;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.UserEmulatorAssignment;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;

import java.util.List;
import java.util.Optional;


public interface UserEmulatorAssignmentService {

    Iterable<UserEmulatorAssignment> listAllUserEmulatorAssignment();

    UserEmulatorAssignment getUserEmulatorAssignmentById(Integer id);

    EmulatorDetails saveUserEmulatorAssignment(UserEmulatorAssignment UserEmulatorAssignment) throws Exception;

    EmulatorDetails updateUserEmulatorAssignment(Long UserEmulatorAssignmentId, UserEmulatorAssignment UserEmulatorAssignment) throws Exception;

    void deleteUserEmulatorAssignment(Integer id);

    LoginResponseModel createToken(JwtRequestModel request) throws Exception;

    UserEmulatorAssignment findByEmail(String email);

    Optional<List<EmulatorDetails>> getEmulatorsByUserId(Long userId);

    void releaseEmulator(Long emulatorId) throws Exception;


}
