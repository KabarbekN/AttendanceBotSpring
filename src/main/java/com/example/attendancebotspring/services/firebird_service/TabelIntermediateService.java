package com.example.attendancebotspring.services.firebird_service;

import com.example.attendancebotspring.components.TelegramBot;
import com.example.attendancebotspring.configurations.BotConfiguration;
import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import com.example.attendancebotspring.repositories.firebird_repos.ITabelIntermediadateRepository;
import com.example.attendancebotspring.repositories.mysql_repos.IKidRepository;
import com.example.attendancebotspring.repositories.mysql_repos.IUserKidRepository;
import com.example.attendancebotspring.services.mysql_service.KidService;
import com.example.attendancebotspring.services.mysql_service.UserKidService;
import com.example.attendancebotspring.services.mysql_service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TabelIntermediateService {


    @Autowired
    TelegramBot telegramBot;


    private final ITabelIntermediadateRepository tabelIntermediadateRepository;
    public TabelIntermediadate getTabelIntermediadateById(Long id){
        return tabelIntermediadateRepository.findById(id).orElse(null);
    }

    public List<TabelIntermediadate> getAllTabelIntermediadate(){
        return tabelIntermediadateRepository.findAll();

    }

    public TabelIntermediadate getTableIntermediadateByStaffId(Long id){
        return tabelIntermediadateRepository.findBySTAFF_ID(id);
    }

    @Scheduled(fixedDelay = 60000)
    public void pollForNewData(){
        LocalDate currentDate = LocalDate.now();
        LocalTime currenTime = LocalTime.now();
        LocalTime oneMinuteAgoTime = currenTime.minusMinutes(1);
        List<TabelIntermediadate> records =
                tabelIntermediadateRepository.findTabelIntermediadateByDATE_PASSAndTIME_PASSIsAfter(
                        Date.valueOf(currentDate), Time.valueOf(oneMinuteAgoTime));

        if (records.size() > 0){
//            TelegramBot telegramBot = new TelegramBot(userService, kidService, userKidService, staffService, botConfiguration,kidRepository );
            telegramBot.sendNotification(records);
        }

    }


}
