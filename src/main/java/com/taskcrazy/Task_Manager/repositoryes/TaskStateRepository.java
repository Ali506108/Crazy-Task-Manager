package com.taskcrazy.Task_Manager.repositoryes;

import com.taskcrazy.Task_Manager.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Long> {


}
