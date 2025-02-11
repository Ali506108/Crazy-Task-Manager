package com.taskcrazy.Task_Manager.repositoryes;

import com.taskcrazy.Task_Manager.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;


@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity , Long> {

    Optional<ProjectEntity> findByName(String name);

    @Query("SELECT p FROM ProjectEntity p")
    Stream<ProjectEntity> streamAllBy();

    @Query("SELECT p FROM ProjectEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT(:name, '%'))")
    Stream<ProjectEntity> streamAllByNameStartsWithIgnoreCase(@Param("name") String name);
}
