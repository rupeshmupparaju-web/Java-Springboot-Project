package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks this class as a REST controller which means it will handle HTTP
                // requests
@RequestMapping("/api") // Base URL for all API endpoints in this controller
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // This endpoint is only for login.
    // We accept username and password from LoginRequest and verify them in the
    // database.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        boolean validUser = userRepository.existsByUsernameAndPassword(
                request.getUsername(),
                request.getPassword());

        if (validUser) {
            return ResponseEntity.ok("Login successful!!");
        } else {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }

    // This endpoint is for registration.
    // We accept all registration fields, validate them, then create a new User
    // object.
    @PostMapping("/register") // This endpoint is for registration. It will create a new user in the database
                              // if the username, gmail, and mobile number are not already taken.
    @Transactional // This annotation is used to ensure that the database transaction is committed
                   // only if the method completes successfully. If any exception occurs during the
                   // method execution, the transaction will be rolled back.
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {

        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String password = request.getPassword() == null ? "" : request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword() == null ? "" : request.getConfirmPassword().trim();
        String firstName = request.getFirstName() == null ? "" : request.getFirstName().trim();
        String middleName = request.getMiddleName() == null ? "" : request.getMiddleName().trim();
        String lastName = request.getLastName() == null ? "" : request.getLastName().trim();
        String gmail = request.getGmail() == null ? "" : request.getGmail().trim();
        String mobileNumber = request.getMobileNumber() == null ? "" : request.getMobileNumber().trim();

        ResponseEntity<String> validation = validateRequiredFields(
                username, password, confirmPassword, firstName, lastName, gmail, mobileNumber);
        if (validation != null) {
            return validation;
        }

        validation = validatePasswords(password, confirmPassword);
        if (validation != null) {
            return validation;
        }

        validation = validateUniqueUsername(username);
        if (validation != null) {
            return validation;
        }

        validation = validateUniqueGmail(gmail);
        if (validation != null) {
            return validation;
        }

        validation = validateUniqueMobileNumber(mobileNumber);
        if (validation != null) {
            return validation;
        }

        User newUser = new User(
                username,
                password,
                firstName,
                middleName.isEmpty() ? null : middleName,
                lastName,
                gmail,
                mobileNumber);

        userRepository.save(newUser);

        return ResponseEntity.ok("Registration successful. Please login.");
    }

    private ResponseEntity<String> validateRequiredFields(
            String username,
            String password,
            String confirmPassword,
            String firstName,
            String lastName,
            String gmail,
            String mobileNumber) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || firstName.isEmpty() || lastName.isEmpty() || gmail.isEmpty() || mobileNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("All fields except middle name are required.");
        }
        return null;
    }

    private ResponseEntity<String> validatePasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Password and confirm password must match.");
        }
        return null;
    }

    private ResponseEntity<String> validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        return null;
    }

    private ResponseEntity<String> validateUniqueGmail(String gmail) {
        if (userRepository.existsByGmail(gmail)) {
            return ResponseEntity.badRequest().body("Gmail is already registered.");
        }
        return null;
    }

    private ResponseEntity<String> validateUniqueMobileNumber(String mobileNumber) {
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            return ResponseEntity.badRequest().body("Mobile number is already registered.");
        }
        return null;
    }

    @PostMapping("/forgot-password") // This endpoint is for forgot password. It will check if the gmail exists in
                                     // the database and send a password reset link to the user's email.
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String gmail = request.getGmail() == null ? "" : request.getGmail().trim();

        if (gmail.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        if (userRepository.existsByGmail(gmail)) {
            return ResponseEntity.ok("Password reset link sent to your email!");
        } else {
            return ResponseEntity.status(404).body("Account not found.");
        }
    }
}