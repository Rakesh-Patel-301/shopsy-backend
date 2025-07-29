package com.example.ecommerce.repository;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    
    List<Address> findByUserAndAddressType(User user, String addressType);
    
    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.addressType = :addressType ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findByUserAndAddressTypeOrdered(@Param("user") User user, @Param("addressType") String addressType);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user AND a.id != :addressId")
    void clearDefaultAddressesForUser(@Param("user") User user, @Param("addressId") Long addressId);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = true WHERE a.id = :addressId")
    void setAsDefaultAddress(@Param("addressId") Long addressId);
    
    boolean existsByUserAndAddressType(User user, String addressType);
    
    long countByUser(User user);
} 