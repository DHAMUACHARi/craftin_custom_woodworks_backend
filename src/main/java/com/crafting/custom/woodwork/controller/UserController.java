package com.crafting.custom.woodwork.controller;

import com.crafting.custom.woodwork.entity.UserDetails;
import com.crafting.custom.woodwork.service.UserService;
import com.crafting.custom.woodwork.serviceimpl.UserServiceImpl;
import com.crafting.custom.woodwork.userdto.LoginRequest;
import com.crafting.custom.woodwork.validator.EmailValidator;
import com.crafting.custom.woodwork.validator.Validator;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OpenAPIDefinition(
		info = @Info(
				title = "Crafting Custom Wood Work", 
				description ="User Details Controller", 
				version = "1.0.0")
		)
@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
    @Autowired
    private UserService userService;
    
    @Autowired
    UserServiceImpl userServiceImpl;
    
    @Autowired
    private Validator validator;
    
    

    /**
     * Create a new user
     */
    
    
    @Operation(summary = "Create a new user", description = "This endpoint creates a new user and returns the user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", 
                         content = @Content(mediaType = "application/json", 
                                            schema = @Schema(implementation = UserDetails.class))),
            @ApiResponse(responseCode = "400", description = "User Creation failed", 
                         content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDetails userDetails) {
    	 log.info("Creating User in createUser() Method");
    	 
    	// Step 1: Validate Email Format
    	    if (!Validator.isValidEmail(userDetails.getEmail())) {
    	        log.info("Invalid email format: " + userDetails.getEmail());
    	        return ResponseEntity.badRequest().body(Map.of("status", "Invalid email format"));
    	    }

    	    // Step 2: Check if Email Domain Has MX Records
    	    if (!EmailValidator.hasValidMXRecords(userDetails.getEmail())) {
    	        log.info("Invalid email domain (No MX records): " + userDetails.getEmail());
    	        return ResponseEntity.badRequest().body(Map.of("status", "Invalid email domain"));
    	    }
    	 Map<String, String> validationErrors = validator.validateUser(userDetails);
    	if (!validationErrors.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("httpStatus", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("isValid", false);
            errorResponse.put("status", "Validation failed");
            errorResponse.put("errors", validationErrors);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        UserDetails createdUser = userService.createUser(userDetails);
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("isValid", createdUser != null);
        response.put("status", createdUser != null ? "User created successfully" : "User creation failed");
        response.put("data", createdUser);

        if (createdUser != null) {
            userService.sendUserCreationEmail(userDetails.getEmail(), userDetails.getFirstName());
            log.info(" Insiede userService Sending mail");
        }
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Integer id) {
        UserDetails user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Get all users
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDetails>> getAllUsers() {
        List<UserDetails> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Update a user by ID
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDetails> updateUser(@PathVariable Integer id, @RequestBody UserDetails updatedDetails) {
        UserDetails updatedUser = userService.updateUser(id, updatedDetails);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Delete a user by ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        UserDetails user = userServiceImpl.findByEmail(loginRequest.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("httpStatus", HttpStatus.NOT_FOUND.value());
            response.put("isValid", false);
            response.put("status", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            response.put("httpStatus", HttpStatus.UNAUTHORIZED.value());
            response.put("isValid", false);
            response.put("status", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        response.put("httpStatus", HttpStatus.OK.value());
        response.put("isValid", true);
        response.put("status", "Login successful");
        response.put("userData", user);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
