package com.example.attendancebotspring.services.mysql_service;


import com.example.attendancebotspring.models.mysql_models.Kid;
import com.example.attendancebotspring.repositories.mysql_repos.IKidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KidService {
    private final IKidRepository iKidRepository;

    public Kid getKidById(Long id){
        return iKidRepository.findById(id).orElse(null);
    }
    public Kid getKidByStaffId(Long id) {return iKidRepository.findByStaff_id(id);}

    public List<Kid> getAllKid(){
        return iKidRepository.findAll();
    }

    public void deleteKidById(Long id){
        iKidRepository.deleteById(id);
    }

    public void saveKid(Kid kid){

       iKidRepository.save(kid);
    }

}
