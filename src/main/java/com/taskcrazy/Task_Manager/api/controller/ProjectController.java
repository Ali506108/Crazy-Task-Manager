package com.taskcrazy.Task_Manager.api.controller;


import com.taskcrazy.Task_Manager.api.dto.AckDto;
import com.taskcrazy.Task_Manager.api.dto.ProjectDto;
import com.taskcrazy.Task_Manager.api.dto.controllerHelpers.ControllerHelper;
import com.taskcrazy.Task_Manager.api.exeption.BadRequestException;
import com.taskcrazy.Task_Manager.api.exeption.NotFoundException;
import com.taskcrazy.Task_Manager.api.factories.ProjectDtoFactory;
import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import com.taskcrazy.Task_Manager.repositoryes.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
@Transactional
@RestController
public class ProjectController {



    private final ProjectDtoFactory dtoFactory;

    private final ProjectRepository projectRepository;

    private static final String FETCH_PROJECTS = "/projects";
    private static final String CREATE_PROJECT = "/api/project";
    private static final String UPDAATE_PROJECT = "/api/project/{projectId}";
    private static final String DELETE_PROJECT = "/api/project/{projectId}";
    private static final String CREATE_OR_UPDATE_PROJECT = "/api/project/{projectId}/update";
    private final ControllerHelper controllerHelper;

    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDto> fetchProjects(@RequestParam(value = "prefix_name", required = false)
                                          Optional<String> optionalPrefixName) {
        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectStream.map(dtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }



    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ResponseEntity<ProjectDto> createAndUpdateProject(
            @RequestParam(value = "project_id" , required = false) Optional<Long> optionalLongId,
            @RequestParam(value = "project_name" , required = false) Optional<String> optionalProjectName
    ) {
        optionalProjectName = optionalProjectName
                .filter(projeName -> !projeName.trim().isEmpty());

        boolean isCreate = !optionalProjectName.isPresent();

        if(isCreate && !optionalProjectName.isPresent()) {
            throw new BadRequestException("Project name cannot be empty");
        }


        final ProjectEntity project =
                optionalLongId
                        .map(controllerHelper::getProjectOrThrowException)
                        .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestException(
                                        String.format("Project \"%s\" already exists.", projectName)
                                );
                            });

                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return ResponseEntity.ok(dtoFactory.makeProjectDto(savedProject));
    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String name) {
        log.info("Creating new project {}", name);
        if(name.trim() .isEmpty()) {
            throw new BadRequestException("Project name cannot be empty");

        }
        log.info("Creating new project {}", name);

        projectRepository
                .findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project %s already exists", name));
                });
        log.info("Creating new project {}", name);


        ProjectEntity saveProject = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(name)
                         .build()
        );

        return dtoFactory.makeProjectDto(saveProject);

    }


    @PatchMapping(UPDAATE_PROJECT)
    public ProjectDto updateProject(@PathVariable("projectId") long projectId ,
                                    @RequestParam String name) {

        log.info("Updating project {}", name);

        ProjectEntity findProject = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Project %s not found", name)));


        projectRepository
                .findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), findProject.getId()))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project %s already exists", name));
                });
        log.info("Creating new project {}", name);


        findProject.setName(name);

        ProjectEntity saveProject = projectRepository.saveAndFlush(findProject);

        return dtoFactory.makeProjectDto(saveProject);

    }



    @DeleteMapping(DELETE_PROJECT)
    public AckDto deleteProject(@PathVariable long projectId) {
        log.info("Deleting project {}", projectId);

        controllerHelper.getProjectOrThrowException(projectId);

        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }



}
