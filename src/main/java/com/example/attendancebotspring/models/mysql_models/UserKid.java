package com.example.attendancebotspring.models.mysql_models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Lombok;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowire;

@Entity
@Data
@Table(name = "user_kid", schema ="attendanceBot")
@NoArgsConstructor
@AllArgsConstructor
public class UserKid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "kid_id")
    private Long kid_id;

}
