package com.crafting.custom.woodwork.service;

import com.crafting.custom.woodwork.entity.UserDetails;
import java.util.List;

public interface UserService {
    UserDetails createUser(UserDetails userDetails);

    UserDetails getUserById(Integer userId);

    List<UserDetails> getAllUsers();

    UserDetails updateUser(Integer userId, UserDetails updatedDetails);

    void deleteUser(Integer userId);
    
    void sendUserCreationEmail(String toEmail, String userName);
}
