package com.example.attendancebotspring.repositories.mysql_repos;

import com.example.attendancebotspring.models.mysql_models.UserKid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface IUserKidRepository extends JpaRepository<UserKid, Long> {
    @Query("select u from UserKid u where u.user_id = ?1")
    List<UserKid> findAllByUser_id(Long id);

    @Query("select u from UserKid u where u.user_id = ?1 and u.kid_id = ?2")
    List<UserKid> findByUser_idAndKid_id(Long user_id, Long kid_id);

    @Transactional
    @Modifying
    @Query("delete from UserKid u where u.user_id = ?1 and u.kid_id = ?2")
    void deleteByUser_idAndKid_id(Long user_id, Long kid_id);


    @Query("select u from UserKid u where u.kid_id = ?1")
    List<UserKid> findAllByKid_id(Long id);
}
