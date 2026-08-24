package com.creatorconnect.repository;

import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    long countByRole(Role role);
}
