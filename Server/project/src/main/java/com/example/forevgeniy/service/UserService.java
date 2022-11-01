package com.example.forevgeniy.service;

import com.example.forevgeniy.dao.entities.User;
import com.example.forevgeniy.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
}
