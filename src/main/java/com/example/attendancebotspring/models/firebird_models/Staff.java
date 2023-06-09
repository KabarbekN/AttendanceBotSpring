package com.example.attendancebotspring.models.firebird_models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "STAFF")
@AllArgsConstructor
@NoArgsConstructor
public class Staff {
    @Id
    @Column(name = "ID_TB_IN")
    private Long ID_TB_IN;
    @Column(name = "FULL_FIO")
    private String FULL_FIO;
    @Column(name = "TABEL_ID")
    private String TABEL_ID;

}
