package com.taskcrazy.Task_Manager.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class TaskStateEntity {


    @Id
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Builder.Default
    private Instant createdAt = Instant.now();

    private String description;

    private long ordinal;

    @OneToMany
    @JoinColumn(name = "task_state_id")
    private List<TaskEntity> tasks = new ArrayList<>();


}
