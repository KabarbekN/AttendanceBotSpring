package com.example.attendancebotspring.controllers;

import com.example.attendancebotspring.models.firebird_models.Staff;
import com.example.attendancebotspring.services.firebird_service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {
    private final StaffService staffService;

    @GetMapping("")
    public ResponseEntity<List<Staff>> getAllStaff(){
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id){
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @DeleteMapping("/delete/{id}")
    public void deleteStaffById(@PathVariable Long id){
        staffService.deleteStaffById(id);
    }
}
