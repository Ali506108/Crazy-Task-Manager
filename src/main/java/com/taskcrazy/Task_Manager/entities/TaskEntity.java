package com.taskcrazy.Task_Manager.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")

public class TaskEntity {

    @Id
    private long id;

    private String title;

    @Builder.Default
    private Instant createdAt = Instant.now();



    private String description;

}
