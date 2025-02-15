package com.taskcrazy.Task_Manager.api.controller;

import com.taskcrazy.Task_Manager.api.dto.TaskStateDto;
import com.taskcrazy.Task_Manager.api.dto.controllerHelpers.ControllerHelper;
import com.taskcrazy.Task_Manager.api.exeption.BadRequestException;
import com.taskcrazy.Task_Manager.api.factories.TaskStateDTOFactory;
import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import com.taskcrazy.Task_Manager.entities.TaskStateEntity;
import com.taskcrazy.Task_Manager.repositoryes.TaskRepository;
import com.taskcrazy.Task_Manager.repositoryes.TaskStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@EnableAsync
@RequiredArgsConstructor
@Transactional
@RestController
public class TaskStateController {

    private final TaskRepository taskRepository;

    private final TaskStateDTOFactory dtoFactory;

    private final TaskStateRepository stateRepository;

    private final ControllerHelper controllerHelper;

    private static final String GET_TASK_STATE = "/api/projects/{projectId}/tasks-state";
    private static final String CREATE_TASK_STATE = "/api/projects/{projectId}/task-states";
    private static final String UPDATE_TASK_STATE = "/api/projects/{projectId}/task-states/{task_state_id}";
    private static final String DELETE_TASK_STATE = "/api/projects/{projectId}/task-states/{task_state_id}";


    @GetMapping(GET_TASK_STATE)
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "projectId") long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project
                .getTaskStates()
                .stream()
                .map(dtoFactory::createTaskStateDto)
                .collect(Collectors.toList());

    }


    @Async
    @Transactional
    @PostMapping(value = CREATE_TASK_STATE)
    public CompletableFuture<ResponseEntity<TaskStateDto>> createTaskState(
            @PathVariable(name = "projectId") long projectId,
            @RequestParam(name = "task_state_name") String taskName
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (taskName.trim().isEmpty()) {
                throw new BadRequestException("Task name cannot be empty");
            }

            ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

            // Initialize task states to prevent LazyInitializationException
            project.getTaskStates().size();

            boolean taskExists = project.getTaskStates().stream()
                    .anyMatch(state -> state.getName().equalsIgnoreCase(taskName));

            if (taskExists) {
                throw new BadRequestException("Task name already exists");
            }

            TaskStateEntity newTaskState = TaskStateEntity.builder()
                    .name(taskName)
                    .project(project)
                    .build();

            TaskStateEntity savedTaskState = stateRepository.saveAndFlush(newTaskState);

            return new ResponseEntity<>(dtoFactory.createTaskStateDto(savedTaskState), HttpStatus.CREATED);
        });
    }


    @Async
    @PutMapping(value = UPDATE_TASK_STATE)
    public CompletableFuture<ResponseEntity<TaskStateDto>> updateTask(
            @PathVariable(name = "projectId") long id,
            @PathVariable(name = "task_state_id") long taskStateId,
            @RequestParam(name = "task_state_name") String changeName
    ) {

        return CompletableFuture.supplyAsync(() -> {
            if (changeName.trim().isEmpty()) {
                throw new BadRequestException("Task name cannot be empty");
            }

            ProjectEntity project = controllerHelper.getProjectOrThrowException(id);
            TaskStateEntity taskState = stateRepository.findById(taskStateId)
                    .orElseThrow(() -> new BadRequestException("Task state not found"));

            boolean taskExists = project.getTaskStates().stream()
                    .anyMatch(state -> state.getName().equalsIgnoreCase(changeName));

            if (taskExists) {
                throw new BadRequestException("Task name already exists");
            }

            taskState.setName(changeName);
            TaskStateEntity updatedTaskState = stateRepository.saveAndFlush(taskState);

            return new ResponseEntity<>(dtoFactory.createTaskStateDto(updatedTaskState), HttpStatus.OK);
        });

    }


    @Async
    @DeleteMapping(DELETE_TASK_STATE)
    public CompletableFuture<ResponseEntity<Void>> deleteTaskState(
            @PathVariable(name = "projectId") long projectId,
            @PathVariable(name = "task_state_id") long taskStateId) {

        return CompletableFuture.runAsync(() -> {
            controllerHelper.getProjectOrThrowException(projectId);
            TaskStateEntity taskState = stateRepository.findById(taskStateId)
                    .orElseThrow(() -> new BadRequestException("Task state not found"));

            stateRepository.delete(taskState);
        }).thenApply(ignored -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


}
