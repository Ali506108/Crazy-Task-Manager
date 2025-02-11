package com.taskcrazy.Task_Manager.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStateDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private long ordinal;

    @NotNull
    @JsonProperty("created_at")
    private Instant createdAt = Instant.now();
}
