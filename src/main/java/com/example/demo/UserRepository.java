package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);

    boolean existsByGmail(String gmail);

    boolean existsByMobileNumber(String mobileNumber); // Checks if a user with the given mobile number already exists
                                                       // and returns true if it exists else false
}