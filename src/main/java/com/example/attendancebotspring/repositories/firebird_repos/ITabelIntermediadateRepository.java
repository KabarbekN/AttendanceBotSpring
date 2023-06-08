package com.example.attendancebotspring.repositories.firebird_repos;

import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITabelIntermediadateRepository extends JpaRepository<TabelIntermediadate, Long> {
}
