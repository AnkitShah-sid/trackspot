package com.trackspot.services;

import com.trackspot.Jwtutils.JwtUserDetailsService;
import com.trackspot.Jwtutils.TokenManager;
import com.trackspot.entities.Admin;
import com.trackspot.entities.User;
import com.trackspot.entities.jwt.JwtRequestModel;
import com.trackspot.entities.jwt.LoginResponseModel;
import com.trackspot.repositories.AdminRepository;
import com.trackspot.repositories.UserRepository;
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

/**
 * Admin service implement.
 */
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Object getCurrentClient() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof Admin) {
                Admin admin = (Admin) authentication.getPrincipal();
                return admin;
            } else if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                return user;
            } else {
                throw new Exception("Not an Admin Or User Token");
            }
        } else {
            throw new Exception("Un-Authenticated Admin token");
        }
    }

    @Override
    public LoginResponseModel createToken(JwtRequestModel request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword())
            );
        } catch (DisabledException e) {
            throw new Exception("AUTH ERROR : " + e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS : " + e + "\n Using credentials : email=" + request.getEmail() + ", pass=" + request.getPassword());
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return new LoginResponseModel(jwtToken);
    }

}
