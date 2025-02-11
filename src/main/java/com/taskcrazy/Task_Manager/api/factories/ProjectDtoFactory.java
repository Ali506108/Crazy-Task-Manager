package com.taskcrazy.Task_Manager.api.factories;

import com.taskcrazy.Task_Manager.api.dto.ProjectDto;
import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity entity) {
        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdateAt())
                .build();
    }
}