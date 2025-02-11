package com.taskcrazy.Task_Manager.api.controller;

import com.taskcrazy.Task_Manager.api.dto.AckDto;
import com.taskcrazy.Task_Manager.api.dto.ProjectDto;
import com.taskcrazy.Task_Manager.api.dto.controllerHelpers.ControllerHelper;
import com.taskcrazy.Task_Manager.api.exeption.BadRequestException;
import com.taskcrazy.Task_Manager.api.exeption.NotFoundException;
import com.taskcrazy.Task_Manager.api.factories.ProjectDtoFactory;
import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import com.taskcrazy.Task_Manager.repositoryes.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {


    @Mock
    private ProjectDtoFactory dtoFactory;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ControllerHelper controllerHelper;

    @InjectMocks
    private ProjectController projectController;

    private ProjectEntity projectEntity;
    private ProjectDto projectDto;



    @BeforeEach
    void setUp() {

        projectEntity  = ProjectEntity.builder()
                .id(1L)
                .name("British")
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Empire")
                .build();


    }


    @Test
    void fetchProjects_withoutPrefix_returnsAllProjects() {

        when(projectRepository.streamAllBy()).thenReturn(Stream.of(projectEntity));
        when(dtoFactory.makeProjectDto(projectEntity)).thenReturn(projectDto);

        List<ProjectDto> allResult = projectController.fetchProjects(Optional.empty());

        assertNotNull(allResult);
        assertEquals(1, allResult.size());
        assertEquals(projectDto, allResult.get(0));

        verify(projectRepository).streamAllBy();
        verify(dtoFactory).makeProjectDto(projectEntity);

    }

    @Test
    void fetchProjects_withPrefix_returnsFilteredProjects() {
        String prefix = "Test";
        when(projectRepository.streamAllByNameStartsWithIgnoreCase(prefix))
                .thenReturn(Stream.of(projectEntity));
        when(dtoFactory.makeProjectDto(projectEntity)).thenReturn(projectDto);

        List<ProjectDto> result = projectController.fetchProjects(Optional.of(prefix));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectDto, result.get(0));

        verify(projectRepository).streamAllByNameStartsWithIgnoreCase(prefix);
        verify(dtoFactory).makeProjectDto(projectEntity);
    }
    @Test
    void createAndUpdateProject_createNewProject_success() {
        // Simulate creation when no project_id is provided and a valid project_name is given.
        String projectName = "New Project";
        // The controller creates a new (empty) project if no ID is provided.
        ProjectEntity newProject = ProjectEntity.builder().build();
        ProjectEntity savedProject = ProjectEntity.builder()
                .id(2L)
                .name(projectName)
                .build();

        // There is no duplicate project found.
        when(projectRepository.findByName(projectName)).thenReturn(Optional.empty());
        // Simulate saving the project.
        when(projectRepository.saveAndFlush(any(ProjectEntity.class))).thenReturn(savedProject);
        when(dtoFactory.makeProjectDto(savedProject)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response =
                projectController.createAndUpdateProject(Optional.empty(), Optional.of(projectName));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(projectDto, response.getBody());

        verify(projectRepository).findByName(projectName);
        verify(projectRepository).saveAndFlush(any(ProjectEntity.class));
        verify(dtoFactory).makeProjectDto(savedProject);
        // Because no project ID is provided, the helper should not be used.
        verifyNoInteractions(controllerHelper);
    }

    @Test
    void createAndUpdateProject_updateExistingProject_success() {
        // Simulate an update when project_id is provided.
        Long projectId = 1L;
        String projectName = "Updated Project";
        ProjectEntity existingProject = ProjectEntity.builder().id(projectId).build();
        ProjectEntity savedProject = ProjectEntity.builder()
                .id(projectId)
                .name(projectName)
                .build();

        when(controllerHelper.getProjectOrThrowException(projectId))
                .thenReturn(existingProject);
        when(projectRepository.findByName(projectName)).thenReturn(Optional.empty());
        when(projectRepository.saveAndFlush(existingProject)).thenReturn(savedProject);
        when(dtoFactory.makeProjectDto(savedProject)).thenReturn(projectDto);

        ResponseEntity<ProjectDto> response =
                projectController.createAndUpdateProject(Optional.of(projectId), Optional.of(projectName));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(projectDto, response.getBody());

        verify(controllerHelper).getProjectOrThrowException(projectId);
        verify(projectRepository).findByName(projectName);
        verify(projectRepository).saveAndFlush(existingProject);
        verify(dtoFactory).makeProjectDto(savedProject);
    }

    @Test
    void createAndUpdateProject_emptyProjectName_throwsBadRequestException() {
        // If the project name is empty (after trimming), a BadRequestException should be thrown.
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> projectController.createAndUpdateProject(Optional.empty(), Optional.of("   ")));
        assertEquals("Project name cannot be empty", exception.getMessage());
    }

    @Test
    void createAndUpdateProject_duplicateProjectName_throwsBadRequestException() {
        // When updating, if a project with the same name exists (with a different id), throw exception.
        Long projectId = 1L;
        String projectName = "Existing Project";
        ProjectEntity existingProject = ProjectEntity.builder().id(projectId).build();
        // Simulate that a different project with the same name already exists.
        ProjectEntity anotherProject = ProjectEntity.builder().id(2L).name(projectName).build();

        when(controllerHelper.getProjectOrThrowException(projectId))
                .thenReturn(existingProject);
        when(projectRepository.findByName(projectName)).thenReturn(Optional.of(anotherProject));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> projectController.createAndUpdateProject(Optional.of(projectId), Optional.of(projectName)));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(controllerHelper).getProjectOrThrowException(projectId);
        verify(projectRepository).findByName(projectName);
    }

    // ===============================
    // createProject() tests
    // ===============================
    @Test
    void createProject_success() {
        String projectName = "New Project";
        ProjectEntity projectToSave = ProjectEntity.builder().name(projectName).build();
        ProjectEntity savedProject = ProjectEntity.builder()
                .id(3L)
                .name(projectName)
                .build();

        when(projectRepository.findByName(projectName)).thenReturn(Optional.empty());
        when(projectRepository.saveAndFlush(any(ProjectEntity.class))).thenReturn(savedProject);
        when(dtoFactory.makeProjectDto(savedProject)).thenReturn(projectDto);

        ProjectDto result = projectController.createProject(projectName);
        assertNotNull(result);
        assertEquals(projectDto, result);

        verify(projectRepository).findByName(projectName);
        verify(projectRepository).saveAndFlush(any(ProjectEntity.class));
        verify(dtoFactory).makeProjectDto(savedProject);
    }

    @Test
    void createProject_emptyName_throwsBadRequestException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> projectController.createProject("   "));
        assertEquals("Project name cannot be empty", exception.getMessage());
    }

    @Test
    void createProject_duplicateName_throwsBadRequestException() {
        String projectName = "Duplicate Project";
        ProjectEntity existingProject = ProjectEntity.builder().id(4L).name(projectName).build();

        when(projectRepository.findByName(projectName)).thenReturn(Optional.of(existingProject));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> projectController.createProject(projectName));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(projectRepository).findByName(projectName);
    }

    // ===============================
    // updateProject() tests
    // ===============================
    @Test
    void updateProject_success() {
        long projectId = 5L;
        String newName = "Updated Project";
        ProjectEntity foundProject = ProjectEntity.builder()
                .id(projectId)
                .name("Old Name")
                .build();
        ProjectEntity savedProject = ProjectEntity.builder()
                .id(projectId)
                .name(newName)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(foundProject));
        when(projectRepository.findByName(newName)).thenReturn(Optional.empty());
        when(projectRepository.saveAndFlush(foundProject)).thenReturn(savedProject);
        when(dtoFactory.makeProjectDto(savedProject)).thenReturn(projectDto);

        ProjectDto result = projectController.updateProject(projectId, newName);
        assertNotNull(result);
        assertEquals(projectDto, result);

        verify(projectRepository).findById(projectId);
        verify(projectRepository).findByName(newName);
        verify(projectRepository).saveAndFlush(foundProject);
        verify(dtoFactory).makeProjectDto(savedProject);
    }

    @Test
    void updateProject_notFound_throwsNotFoundException() {
        long projectId = 6L;
        String newName = "NonExistent";

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> projectController.updateProject(projectId, newName));
        assertTrue(exception.getMessage().contains("not found"));

        verify(projectRepository).findById(projectId);
    }

    @Test
    void updateProject_duplicateName_throwsBadRequestException() {
        long projectId = 7L;
        String newName = "Duplicate";
        ProjectEntity foundProject = ProjectEntity.builder()
                .id(projectId)
                .name("Old Name")
                .build();
        // Simulate that another project with the same new name exists.
        ProjectEntity anotherProject = ProjectEntity.builder()
                .id(8L)
                .name(newName)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(foundProject));
        when(projectRepository.findByName(newName)).thenReturn(Optional.of(anotherProject));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> projectController.updateProject(projectId, newName));
        assertTrue(exception.getMessage().contains("already exists"));

        verify(projectRepository).findById(projectId);
        verify(projectRepository).findByName(newName);
    }

    // ===============================
    // deleteProject() tests
    // ===============================
    @Test
    void deleteProject_success() {
        long projectId = 9L;
        // Simulate that the project exists by having the helper return an entity.
        when(controllerHelper.getProjectOrThrowException(projectId))
                .thenReturn(ProjectEntity.builder().id(projectId).build());

        // Call the delete endpoint.
        AckDto ack = projectController.deleteProject(projectId);

        // Verify that the helper was called and the repository deletion happened.
        verify(controllerHelper).getProjectOrThrowException(projectId);
        verify(projectRepository).deleteById(projectId);
        // Check that the ACK is true.
        assertTrue(ack.isAnswer());
    }
}
