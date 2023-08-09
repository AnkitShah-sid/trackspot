package com.trackspot.controllers;

import com.trackspot.Jwtutils.JwtUserDetailsService;
import com.trackspot.entities.Admin;
import com.trackspot.entities.User;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.services.AdminService;
import com.trackspot.services.ClientService;
import com.trackspot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class baseController {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentClient() {
        try {
            return ResponseEntity.ok(clientService.getCurrentClient());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error "+e);
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<?> login(@RequestBody JwtRequestModel request) {
        try {
            LoginResponseModel responseModel = clientService.createToken(request);
            return ResponseEntity.ok(responseModel);
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error "+e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> passwordBody, Authentication authentication) {
        try {
            String password = (String) passwordBody.get("password");
            if (authentication != null && authentication.isAuthenticated()) {
                if (password != null && !password.isBlank()) {
                    if (authentication.getPrincipal() instanceof Admin) {
                        Admin admin = (Admin) authentication.getPrincipal();
                        adminService.setPassword(admin.getId(), password);
                    } else if (authentication.getPrincipal() instanceof User) {
                        User user = (User) authentication.getPrincipal();
                        System.out.println("password : " + password);
                        userService.setPassword(user.getId(), password);
                    }
                } else {
                    throw new Exception("password Cannot be empty");
                }
            } else {
                throw new Exception("Un-Authenticated Admin token");
            }
            return ResponseEntity.ok("Password Reset Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error " + e);
        }

    }
}

