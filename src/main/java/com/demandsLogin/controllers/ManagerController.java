package com.demandsLogin.controllers;

import com.demandsLogin.domain.user.User; // Keep this for internal logic
import com.demandsLogin.dto.UserDto; // Add this import
import com.demandsLogin.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final UserService userService;

    public ManagerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/subordinates")
    @PreAuthorize("hasRole('MANAGER')")
    public List<UserDto> getSubordinates(@RequestParam String managerId) { // Return List<UserDto>
        System.out.println("Buscando manager: " + managerId);
        User manager = userService.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + managerId));
        System.out.println("Manager encontrado: " + manager.getName());
        System.out.println("Qtd subordinados: " + manager.getSubordinates().size());

        // Convert the List<User> to List<UserDto> to avoid circular references
        return UserDto.fromUsers(manager.getSubordinates());
    }
}