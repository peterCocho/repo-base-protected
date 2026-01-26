package com.churninsight_dev.backend_api.controller;

import com.churninsight_dev.backend_api.model.User;
import com.churninsight_dev.backend_api.model.Profile;
import com.churninsight_dev.backend_api.repository.UserRepository;
import com.churninsight_dev.backend_api.dto.UserInfoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint para obtener información del usuario actual
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser() {
        // Obtener el usuario autenticado desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        // Buscar el usuario en la base de datos por email
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Retornar 404 si el usuario no se encuentra
            return ResponseEntity.notFound().build();
        }
        
        // Obtener el usuario encontrado
        User user = userOpt.get();
        // Crear el DTO con la información del usuario
        UserInfoDTO userInfo = new UserInfoDTO(
            user.getId(),
            user.getUserName(),
            user.getEmail(),
            user.getCompanyName(),
            user.getProfiles()
        );
        
        // Retornar la información del usuario con código 200
        return ResponseEntity.ok(userInfo);
    }

    // Solo un ADMIN puede crear otro ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@RequestBody User user) {
        // Asignar el perfil de ADMIN al nuevo usuario
        user.setProfiles(Set.of(Profile.ROLE_ADMIN));
        // Guardar el usuario en la base de datos
        User saved = userRepository.save(user);
        // Retornar el usuario creado con código 201
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
