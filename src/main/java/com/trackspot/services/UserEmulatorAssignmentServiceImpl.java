package com.trackspot.services;

import com.trackspot.entities.EmulatorDetails;
import com.trackspot.entities.User;
import com.trackspot.entities.UserEmulatorAssignment;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.repositories.EmulatorDetailsRepository;
import com.trackspot.services.emulator.EmulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserEmulatorAssignmentServiceImpl implements UserEmulatorAssignmentService{

    @Autowired
    private EmulatorDetailsRepository emulatorDetailsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmulatorService emulatorService;

    @Override
    public Iterable<UserEmulatorAssignment> listAllUserEmulatorAssignment() {
        return null;
    }

    @Override
    public UserEmulatorAssignment getUserEmulatorAssignmentById(Integer id) {
        return null;
    }

    //create UserEmulatorAssignment
    public EmulatorDetails saveUserEmulatorAssignment(UserEmulatorAssignment userEmulatorAssignment) throws Exception {
        Optional<User> optionalUser = userService.findById(userEmulatorAssignment.getUser().getId());
        Optional<EmulatorDetails> optionalEmulatorDetails = emulatorService.findById(userEmulatorAssignment.getEmulatorDetails().getId());

        if (optionalEmulatorDetails.isPresent() && optionalUser.isPresent()) {
            EmulatorDetails emulatorDetails = optionalEmulatorDetails.get();
            User user = optionalUser.get();

            if (emulatorDetails.getUser() != null && !emulatorDetails.getUser().equals(user)) {
                throw new Exception("Emulator is already assigned to another user.");
            }

            emulatorDetails.setUser(user);
            return emulatorDetailsRepository.save(emulatorDetails);
        } else {
            throw new Exception("Sub entities not present in Database");
        }
    }

    //upDate UserEmulatorAssignment
    @Override
    public EmulatorDetails updateUserEmulatorAssignment(Long emulatorDetailsId, UserEmulatorAssignment userEmulatorAssignment) throws Exception {
        EmulatorDetails existingEmulatorDetails = emulatorDetailsRepository.findById(emulatorDetailsId).orElse(null);
        if (existingEmulatorDetails == null) {
            throw new Exception("EmulatorDetails Not found");
        }
        User user = userService.findById(userEmulatorAssignment.getUser().getId()).orElse(null);
        existingEmulatorDetails.setUser(user);
        return emulatorDetailsRepository.save(existingEmulatorDetails);
    }

    @Override
    public Optional<List<EmulatorDetails>> getEmulatorsByUserId(Long userId) {
        return emulatorDetailsRepository.findByUser_Id(userId);
    }

    @Override
    public void releaseEmulator(Long emulatorId) throws Exception {
        Optional<EmulatorDetails> emulatorDetails = emulatorDetailsRepository.findById(emulatorId);
        if (emulatorDetails.isPresent()) {
            EmulatorDetails emulator = emulatorDetails.get();
            emulator.setUser(null); // Set user to null
            emulatorDetailsRepository.save(emulator);
        } else {
            throw new Exception("Sub entities not present in Database");
        }
    }

    @Override
    public void deleteUserEmulatorAssignment(Integer id) {

    }

    @Override
    public LoginResponseModel createToken(JwtRequestModel request) throws Exception {
        return null;
    }

    @Override
    public UserEmulatorAssignment findByEmail(String email) {
        return null;
    }

}
