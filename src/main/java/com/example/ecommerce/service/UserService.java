package com.example.ecommerce.service;

import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            existingUser.setPhone(userDetails.getPhone());
            existingUser.setAddress(userDetails.getAddress());
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found");
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public List<User> getAllUsersList() {
        return userRepository.findAll();
    }
    
    // Admin dashboard methods
    public Map<String, Long> getUserStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countByEnabledTrue());
        stats.put("adminUsers", userRepository.countByRole(User.UserRole.ADMIN));
        stats.put("customerUsers", userRepository.countByRole(User.UserRole.CUSTOMER));
        return stats;
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByEnabledTrue();
    }
    
    public List<User> getRecentUsers() {
        return userRepository.findTop10ByOrderByCreatedAtDesc();
    }
} 