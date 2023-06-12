package com.example.attendancebotspring.services.firebird_service;

import com.example.attendancebotspring.models.firebird_models.Staff;
import com.example.attendancebotspring.repositories.firebird_repos.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StaffService {
    private final IStaffRepository staffRepository;

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    public List<Staff> getAllStaff(){
        return staffRepository.findAll();
    }

    public void deleteStaffById(Long id){
        staffRepository.deleteById(id);
    }
}
