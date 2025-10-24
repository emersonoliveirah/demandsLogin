package com.demandsLogin.dto;

import com.demandsLogin.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for User to prevent circular references during JSON serialization,
 * especially when dealing with manager/subordinates relationships.
 * Implemented as a Record for conciseness and immutability.
 */
public record UserDto(
        String id,
        String name,
        String email,
        String role // Using String to avoid potential serialization issues with enums if needed
        // Note: Not including 'team' or 'password' for security and clarity
) {

    /**
     * Constructor that takes a User entity and maps relevant fields.
     * Excludes manager and subordinates to break the cycle.
     *
     * @param user The User entity to map from.
     */
    public UserDto(User user) {
        this(
                user != null ? user.getId() : null,
                user != null ? user.getName() : null,
                user != null ? user.getEmail() : null,
                user != null && user.getRole() != null ? user.getRole().name() : null
                // Note: team and password are intentionally omitted
        );
    }

    /**
     * Static helper method to convert a list of User entities to UserDtos.
     *
     * @param users The list of User entities.
     * @return A list of UserDto records. Returns an empty list if input is null.
     */
    public static List<UserDto> fromUsers(List<User> users) {
        if (users == null) {
            return List.of(); // Return empty list instead of null
        }
        return users.stream()
                .map(UserDto::new) // Uses the constructor UserDto(User user)
                .collect(Collectors.toList());
    }
}