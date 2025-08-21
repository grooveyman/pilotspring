package com.grooveyman.crud.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grooveyman.crud.model.User;
import com.grooveyman.crud.repository.UserRepository;
import com.grooveyman.crud.util.ErrorHandler;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            System.out.println("Error occurred while fetching user: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            if (user.getName() == null || user.getName().isEmpty() || user.getEmail() == null
                    || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Name/email/password is mandatory");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An error occurred while creating the user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorHandler.errorResponse("User with email " + user.getEmail() + " already exists"));
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User user){
        try{
            System.out.println("Attempting login for user: " + user.getEmail());
            if(user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()){
                throw new IllegalArgumentException("Email and password are required for login");
            }

            User userFound = userRepository.findByEmail(user.getEmail()).orElseThrow(()-> new EntityNotFoundException("User not found with email: " + user.getEmail()));
            if(!passwordEncoder.matches(user.getPassword(), userFound.getPassword())){
                throw new IllegalArgumentException("Invalid password");
            }
            return ResponseEntity.ok("User logged in successfully");

        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorHandler.errorResponse("An error occurred while logging in: " + e.getMessage()));
        }
        catch(Exception e){
            System.out.println("Error occurred while logging in: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorHandler.errorResponse("An error occurred while logging in: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            var userResponse = userRepository.findById(id);
            if (!userResponse.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorHandler.errorResponse("User not found"));
            }
            User user = userResponse.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            User updatedUser = userRepository.save(user);
            System.out.println("updated user: " + updatedUser);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorHandler.errorResponse("An error occurred while updating the user: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id: "+id+" Not found"));
            userRepository.delete(user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ErrorHandler.errorResponse("User " + id +  " deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorHandler.errorResponse("An error occurred while deleting the user: " + e.getMessage()));
        }
       
    }

}
