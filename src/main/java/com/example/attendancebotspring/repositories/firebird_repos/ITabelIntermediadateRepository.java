package com.example.attendancebotspring.repositories.firebird_repos;

import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface ITabelIntermediadateRepository extends JpaRepository<TabelIntermediadate, Long> {

        @Query("select t from TabelIntermediadate t where t.DATE_PASS = ?1 and t.TIME_PASS > ?2")
        List<TabelIntermediadate> findTabelIntermediadateByDATE_PASSAndTIME_PASSIsAfter(Date currentDate, Time timeOneMinuteAgo);
        @Query("select t from TabelIntermediadate t where t.STAFF_ID = ?1")
        TabelIntermediadate findBySTAFF_ID(Long id);
}
