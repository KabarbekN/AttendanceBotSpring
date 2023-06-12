package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.Kid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IKidRepository extends JpaRepository<Kid, Long> {
    @Query("select k from Kid k where k.staff_id = ?1")
    Kid findByStaff_id(Long id);
}
