package io.flik.app.services;

import io.flik.app.DTO.UserDTO;
import io.flik.app.auth.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    public UserDTO getUserDto(){
        User user = getCurrentUser();
        return new UserDTO(
                user.getName(),
                user.getPassword()
        );
    }
}

