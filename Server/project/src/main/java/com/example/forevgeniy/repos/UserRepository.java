package com.example.forevgeniy.repos;

import com.example.forevgeniy.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);
}
 