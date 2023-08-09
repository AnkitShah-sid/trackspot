package com.trackspot.services;

import com.trackspot.Jwtutils.JwtUserDetailsService;
import com.trackspot.Jwtutils.TokenManager;
import com.trackspot.entities.Admin;
import com.trackspot.entities.User;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.repositories.AdminRepository;
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

import java.util.Optional;

/**
 * Admin service implement.
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    User user = new User();

    @Override
    public Iterable<Admin> listAllAdmin() {
        return adminRepository.findAll();
    }

    @Override
    public Admin getAdminById(Integer id) {
        return adminRepository.findById(id).get();
    }

    @Override
    public Admin getCurrentAdmin() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof Admin) {
                Admin admin = (Admin) authentication.getPrincipal();
                return admin;
            } else {
                throw new Exception("Not an Admin Token");
            }
        } else {
            throw new Exception("Un-Authenticated Admin token");
        }
    }

    @Override
    public Admin setPassword(Integer id, String password) throws Exception {
        try {
            Optional<Admin> optionalAdmin = adminRepository.findById(id);
            if(optionalAdmin.isPresent()){
                Admin admin = optionalAdmin.get();
                String hashedPassword = passwordEncoder.encode(password);
                admin.setPassword(hashedPassword);
                return adminRepository.save(admin);
            } else {
                throw new Exception("User not Found");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    @Override
    public Admin saveAdmin(Admin admin) {
        String hashedPassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(hashedPassword);
        user.setCreatedBy(admin.getFirstName() + " " + admin.getLastName());
        return adminRepository.save(admin);
    }

    @Override
    public void deleteAdmin(Integer id) {
        adminRepository.deleteById(id);
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

}
