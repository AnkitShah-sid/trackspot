package com.trackspot.controllers;

import com.trackspot.entities.UpdateUserStatus;
import com.trackspot.entities.User;
import com.trackspot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (DataIntegrityViolationException e) {
            // Duplicate entry exception handling
            String errorMessage = "User with the provided email already exists.";
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (Exception e) {
            // Other generic exceptions
            String errorMessage = "An error occurred while saving the user.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        System.out.println("DATA 2 : " + user.toString());
        try {
            User userUpdated= userService.updateUser(user.getId(), user);
            return ResponseEntity.ok("User updated successfully " + userUpdated);
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/change-status")
    public ResponseEntity<String> updateStatus(@RequestBody UpdateUserStatus updateStatus) {
        try {
            boolean statusUpdated = userService.updateStatus(updateStatus);
            if(statusUpdated){
                return ResponseEntity.ok("Status updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Something Went Wrong");
            }
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") long userId) {
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
