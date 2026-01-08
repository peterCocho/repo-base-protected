package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Solo un ADMIN puede crear otro ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        user.setProfiles(Set.of(Profile.ROLE_ADMIN));
        User saved = userRepository.save(user);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
