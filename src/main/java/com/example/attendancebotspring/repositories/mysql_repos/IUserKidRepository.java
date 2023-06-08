package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.UserKid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserKidRepository extends JpaRepository<UserKid, Long> {
}
