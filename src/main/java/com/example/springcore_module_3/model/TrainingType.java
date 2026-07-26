package com.example.springcore_module_3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Setter
@Getter
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingTypeId;

    @Column(nullable = false, unique = true)
    private String trainingTypeName;

    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

}
