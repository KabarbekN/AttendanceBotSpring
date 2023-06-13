package com.example.attendancebotspring.services.mysql_service;

import com.example.attendancebotspring.models.mysql_models.Language;
import com.example.attendancebotspring.repositories.mysql_repos.ILanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final ILanguageRepository iLanguageRepository;

    public void saveLanguage(Language language){
        iLanguageRepository.save(language);
    }

    public Language findByUserIdLanguage(Long chatId){
        return iLanguageRepository.findById(chatId).orElse(null);
    }

}
