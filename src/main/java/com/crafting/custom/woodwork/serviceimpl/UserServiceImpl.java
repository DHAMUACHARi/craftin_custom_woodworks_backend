package com.crafting.custom.woodwork.serviceimpl;

import com.crafting.custom.woodwork.entity.UserDetails;
import com.crafting.custom.woodwork.repository.UserDetailsRepository;
import com.crafting.custom.woodwork.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDetailsRepository userRepository;
    
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserDetails createUser(UserDetails userDetails) {
        userDetails.setCreateTimestamp(LocalDateTime.now());
        userDetails.setModifiedTimestamp(LocalDateTime.now());
        return userRepository.save(userDetails);
    }

    @Override
    public UserDetails getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public List<UserDetails> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails updateUser(Integer userId, UserDetails updatedDetails) {
        Optional<UserDetails> existingUserOpt = userRepository.findById(userId);

        if (existingUserOpt.isPresent()) {
            UserDetails existingUser = existingUserOpt.get();
            existingUser.setFirstName(updatedDetails.getFirstName());
            existingUser.setLastName(updatedDetails.getLastName());
            existingUser.setEmail(updatedDetails.getEmail());
            existingUser.setPhoneNumber(updatedDetails.getPhoneNumber());
            existingUser.setAddress(updatedDetails.getAddress());
            existingUser.setUserType(updatedDetails.getUserType());
            existingUser.setModifiedTimestamp(LocalDateTime.now());
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
    
    public UserDetails findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public void sendUserCreationEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("virtusacontent@gmail.com");  // Your email
        message.setTo(toEmail);
        message.setSubject("User Account Created Successfully");
        message.setText("Dear " + userName + ",\n\nYour account has been successfully created.\n\nRegards,\nTeam");

        mailSender.send(message);
    }
}
