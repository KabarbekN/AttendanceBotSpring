package com.example.attendancebotspring.controllers;


import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import com.example.attendancebotspring.services.firebird_service.TabelIntermediateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tabint")
@RequiredArgsConstructor
public class TabelIntermediadateController {
    private final TabelIntermediateService tabelIntermediateService;


    @GetMapping("")
    public ResponseEntity<Integer> getAllTableIntermediadate(){
        return ResponseEntity.ok(tabelIntermediateService.getAllTabelIntermediadate().size());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabelIntermediadate> getTabelIntermediadateById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(tabelIntermediateService.getTabelIntermediadateById(id));
    }

}
