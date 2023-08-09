package com.trackspot.Security;

import com.trackspot.entities.Admin;
import com.trackspot.entities.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Retrieve the currently logged-in admin or user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Customize the logic to get the desired value for createdBy
            String createdBy = ""; // Initialize with an empty string or default value

            if (authentication.getPrincipal() instanceof Admin) {
                Admin admin = (Admin) authentication.getPrincipal();
                createdBy = admin.getFirstName() + " " + admin.getLastName();
            } else if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                createdBy = user.getFirstName() + " " + user.getLastName();
            }

            return Optional.of(createdBy);
        }

        return Optional.empty();
    }
}
