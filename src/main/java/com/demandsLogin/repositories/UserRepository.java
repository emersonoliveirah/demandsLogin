package com.demandsLogin.repositories;

import com.demandsLogin.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.manager.id = :managerId AND u.team.id = :teamId")
    List<User> findByManagerIdAndTeamId(@Param("managerId") String managerId, @Param("teamId") String teamId);

}