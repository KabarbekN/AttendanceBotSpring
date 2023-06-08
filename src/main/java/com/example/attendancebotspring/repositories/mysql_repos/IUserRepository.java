package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

}
