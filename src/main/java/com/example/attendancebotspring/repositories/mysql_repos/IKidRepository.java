package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.Kid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IKidRepository extends JpaRepository<Kid, Long> {
}
