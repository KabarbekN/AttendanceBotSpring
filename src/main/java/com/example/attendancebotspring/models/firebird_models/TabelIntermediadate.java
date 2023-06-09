package com.example.attendancebotspring.models.firebird_models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Entity
@Data
@Table(name = "TABEL_INTERMEDIADATE")
@AllArgsConstructor
@NoArgsConstructor
public class TabelIntermediadate {
    @Id
    @Column(name = "ID_TB_IN")
    private Long ID_TB_IN;

    @Column(name = "STAFF_ID")
    private Long STAFF_ID;
    @Column(name = "TYPE_PASS")
    private int TYPE_PASS;
    @Column(name = "DATE_PASS")
    private Date DATE_PASS;
    @Column(name = "TIME_PASS")
    private Time TIME_PASS;

}
