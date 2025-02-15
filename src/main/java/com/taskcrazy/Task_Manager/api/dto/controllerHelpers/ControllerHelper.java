package com.taskcrazy.Task_Manager.api.dto.controllerHelpers;

import com.taskcrazy.Task_Manager.api.exeption.NotFoundException;
import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import com.taskcrazy.Task_Manager.repositoryes.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ControllerHelper {

    // Declared final so that it’s injected via constructor
    private final ProjectRepository projectRepository;

    public ProjectEntity getProjectOrThrowException(Long id) {
        return projectRepository.findByIdWithTaskStates(id)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

}
