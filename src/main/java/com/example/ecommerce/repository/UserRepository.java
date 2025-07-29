package com.example.ecommerce.repository;

import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Additional methods for UserService
    Optional<User> findByUsername(String username);
    
    // Admin dashboard methods
    Long countByEnabledTrue();
    Long countByRole(User.UserRole role);
    List<User> findByEnabledTrue();
    List<User> findTop10ByOrderByCreatedAtDesc();
} 