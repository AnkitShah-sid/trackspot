package com.trackspot.Jwtutils;

import com.trackspot.entities.Admin;
import com.trackspot.entities.User;
import com.trackspot.repositories.AdminRepository;
import com.trackspot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            return admin;
        } else {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return user;
            } else {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
        }
    }
}
