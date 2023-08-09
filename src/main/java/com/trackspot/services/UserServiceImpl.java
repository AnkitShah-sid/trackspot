package com.trackspot.services;

import com.trackspot.Jwtutils.JwtUserDetailsService;
import com.trackspot.Jwtutils.TokenManager;
import com.trackspot.entities.*;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.repositories.UserRepository;
import com.trackspot.services.emulator.EmulatorService;
import com.trackspot.services.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmulatorService emulatorService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Override
    public Iterable<User> listAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }

    //create user
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank() && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        }
        User createdUser = userRepository.save(user);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                if (authentication.getPrincipal() instanceof Admin) {
                    Admin admin = (Admin) authentication.getPrincipal();
                    String userToken = tokenManager.generateJwtToken(createdUser);
                    String registrationLink =  "http://149.28.69.114/reset-password?token=" + userToken;
                    Map<String, Object> templateModel = new HashMap<>();
                    templateModel.put("recipientName", user.getFirstName() + " " + user.getLastName());
                    templateModel.put("registrationLink", registrationLink);
                    templateModel.put("senderName", admin.getFirstName() + " " + admin.getLastName());
                    emailService.sendMessageUsingFreemarkerTemplate(
                            user.getEmail(),
                            "Complete Trackspot Registration",
                            templateModel
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error sending Mail : " + e);
        }
        return createdUser;
    }

    //upDate User
    @Override
    public User updateUser(Long userId, User user) throws Exception {
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            throw new Exception("User Not found");
        }
        if (user.getPassword() != null && !user.getPassword().isBlank() && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        } else {
            user.setPassword(existingUser.getPassword());
        }
        user.setStatus(existingUser.getStatus());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            throw new Exception("User Not found");
        }
        for (EmulatorDetails emulator : existingUser.getEmulators()) {
            emulator.setUser(null);
            emulatorService.saveOrUpdateEmulatorDetail(emulator);
        }
        userRepository.delete(existingUser);
    }

    @Override
    public LoginResponseModel createToken(JwtRequestModel request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword())
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return new LoginResponseModel(jwtToken);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            EmulatorCount emulatorCount =  emulatorService.getActiveAndAllEmulatorsByUserId(user.getId());
            user.setEmulatorCount(emulatorCount);
        }
        return users;
    }

    @Override
    public boolean updateStatus(UpdateUserStatus updateUserStatus) throws Exception {
        try {
            Optional<User> userData = userRepository.findById(updateUserStatus.getId());
            if (userData.isPresent()) {
                User user = userData.get();
                if (user.getStatus() != updateUserStatus.getStatus()) {
                    user.setStatus(updateUserStatus.getStatus());
                    userRepository.save(user);
                    return true;
                }
                return true;
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            System.out.println("ERROR : " + e);
            throw new Exception(e);
        }
    }

    @Override
    public User getUser(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            EmulatorCount emulatorCount =  emulatorService.getActiveAndAllEmulatorsByUserId(user.getId());
            user.setEmulatorCount(emulatorCount);
        }
        return user;
    }

    @Override
    public User setPassword(Long id, String password) throws Exception {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                String hashedPassword = passwordEncoder.encode(password);
                user.setPassword(hashedPassword);
                return userRepository.save(user);
            } else {
                throw new Exception("User not Found");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
