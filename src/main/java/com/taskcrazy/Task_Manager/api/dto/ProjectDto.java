package com.taskcrazy.Task_Manager.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private long id;

    private String name;

    @JsonProperty("created_at")
    private Instant createdAt = Instant.now();

    @JsonProperty("update_at")
    private Instant updatedAt = Instant.now();
}
