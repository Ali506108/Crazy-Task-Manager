package com.taskcrazy.Task_Manager.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {


    private long id;

    private String title;

    private Instant createdAt = Instant.now();

    private String description;
}
