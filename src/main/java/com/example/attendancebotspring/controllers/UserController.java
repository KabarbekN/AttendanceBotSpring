package com.example.attendancebotspring.controllers;

import com.example.attendancebotspring.repositories.mysql_repos.IUserRepository;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    final IUserRepository userRepository;

    public UserController(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
