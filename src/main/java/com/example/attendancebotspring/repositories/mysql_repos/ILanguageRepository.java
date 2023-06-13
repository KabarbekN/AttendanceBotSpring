package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ILanguageRepository extends JpaRepository<Language, Long> {
}
