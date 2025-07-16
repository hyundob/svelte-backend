package com.example.template_demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    //Getter and Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //Field
    private Integer Idx;

    private String id;
    private String pw;
}
