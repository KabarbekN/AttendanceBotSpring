package com.example.attendancebotspring.repositories.firebird_repos;

import com.example.attendancebotspring.models.firebird_models.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStaffRepository extends JpaRepository<Staff, Long> {

}
