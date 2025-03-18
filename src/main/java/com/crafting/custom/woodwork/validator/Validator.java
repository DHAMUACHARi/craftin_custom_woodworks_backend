package com.crafting.custom.woodwork.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.crafting.custom.woodwork.entity.UserDetails;

@Component
public class Validator {
	private static final Logger log = LoggerFactory.getLogger(Validator.class);
	
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
		   public Map<String, String> validateUser(UserDetails userDetails) {
			   log.info("Inside validateUser Method in Validator class.");
			   
		        Map<String, String> errors = new HashMap<>();

		        if (userDetails.getFirstName() == null || userDetails.getFirstName().trim().isEmpty()) {
		            errors.put("name", "Name is required");
		        }
		        if (userDetails.getEmail() == null || !userDetails.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
		            errors.put("email", "Invalid email format");
		        }
		        if (userDetails.getPassword() == null || userDetails.getPassword().length() < 6) {
		            errors.put("password", "Password must be at least 6 characters long");
		        }

		        return errors;
	}
		   public static boolean isValidEmail(String email) {
		        return EMAIL_PATTERN.matcher(email).matches();
		    }
}

