package com.taskcrazy.Task_Manager.repositoryes;

import com.taskcrazy.Task_Manager.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<TaskEntity , Long> {
}
