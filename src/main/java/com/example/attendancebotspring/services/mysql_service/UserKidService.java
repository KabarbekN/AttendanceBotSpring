package com.example.attendancebotspring.services.mysql_service;


import com.example.attendancebotspring.models.mysql_models.UserKid;
import com.example.attendancebotspring.repositories.mysql_repos.IUserKidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserKidService {
    private final IUserKidRepository iUserKidRepository;

    public UserKid getUserKidById(Long id){
        return iUserKidRepository.findById(id).orElse(null);
    }

    public List<UserKid> getAllUserKid() {
        return iUserKidRepository.findAll();
    }

    public void deleteUserKidById(Long id){
        iUserKidRepository.deleteById(id);
    }

    public void saveUserKid(UserKid userKid){
        iUserKidRepository.save(userKid);
    }

    public List<UserKid> getAllUserKidByUserId(Long id){return iUserKidRepository.findAllByUser_id(id);}

    public boolean checkIfUserKidExist(Long user_id, Long kid_id){
        List<UserKid> userKids = iUserKidRepository.findByUser_idAndKid_id(user_id, kid_id);
        return userKids.size() != 0;
    }


    public void deleteByUserIdAndKidId(Long user_id, Long kid_id){
        iUserKidRepository.deleteByUser_idAndKid_id(user_id, kid_id);
    }

    public List<UserKid> findAllByKidId(Long kidId){
        return iUserKidRepository.findAllByKid_id(kidId);
    }
}
