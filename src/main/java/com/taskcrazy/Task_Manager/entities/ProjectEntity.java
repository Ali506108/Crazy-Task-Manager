package com.taskcrazy.Task_Manager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(unique = true)
    private String name;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updateAt = Instant.now();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id" , referencedColumnName = "id")
    private List<TaskStateEntity> taskStates = new ArrayList<>();


}
