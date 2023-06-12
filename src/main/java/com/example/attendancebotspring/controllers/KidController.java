package com.example.attendancebotspring.controllers;

import com.example.attendancebotspring.models.mysql_models.Kid;
import com.example.attendancebotspring.services.mysql_service.KidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kid")
@RequiredArgsConstructor
public class KidController {
    private final KidService kidService;
    @GetMapping("")
    public ResponseEntity<List<Kid>> getAllKid(){
        return ResponseEntity.ok(kidService.getAllKid());
    }
}
