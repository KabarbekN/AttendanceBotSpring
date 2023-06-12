package com.example.attendancebotspring.services.mysql_service;


import com.example.attendancebotspring.models.mysql_models.User;
import com.example.attendancebotspring.repositories.mysql_repos.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository iUserRepository;

    public User getUserById(Long id){
        return iUserRepository.findById(id).orElse(null);
    }

    public List<User> getAllUser(){
        return iUserRepository.findAll();
    }

    public void deleteUserById(Long id){
        iUserRepository.deleteById(id);
    }

    public void saveUser(User user){
        iUserRepository.save(user);
    }
}
