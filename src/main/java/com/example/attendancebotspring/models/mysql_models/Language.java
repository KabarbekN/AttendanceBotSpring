package com.example.attendancebotspring.models.mysql_models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_language", schema = "attendanceBot")
@AllArgsConstructor
@NoArgsConstructor
public class Language {
    @Id
    @Column(name = "userId")
    private Long id;

    @Column(name = "language")
    private String language;


}
