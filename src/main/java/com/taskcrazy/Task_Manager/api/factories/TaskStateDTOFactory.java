package com.taskcrazy.Task_Manager.api.factories;

import com.taskcrazy.Task_Manager.api.dto.TaskStateDto;
import com.taskcrazy.Task_Manager.entities.TaskStateEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskStateDTOFactory {

    public TaskStateDto createTaskStateDto(TaskStateEntity taskState) {

        return TaskStateDto.builder()
                .id(taskState.getId())
                .name(taskState.getName())
                .ordinal(taskState.getOrdinal())
                .createdAt(taskState.getCreatedAt())
                .build();
    }
}
