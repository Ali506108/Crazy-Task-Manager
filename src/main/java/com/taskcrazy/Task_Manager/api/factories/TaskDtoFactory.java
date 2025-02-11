package com.taskcrazy.Task_Manager.api.factories;

import com.taskcrazy.Task_Manager.api.dto.TaskDto;
import com.taskcrazy.Task_Manager.entities.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto createTaskDto(TaskEntity taskEntity) {

        return TaskDto.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .createdAt(taskEntity.getCreatedAt())
                .description(taskEntity.getDescription())
                .build();

    }
}
