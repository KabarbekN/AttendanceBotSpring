package com.example.attendancebotspring.models.mysql_models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "kid")
@NoArgsConstructor
@AllArgsConstructor
public class Kid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "table_id")
    private Long table_id;

    @Column(name = "staff_id")
    private Long staff_id;
}
