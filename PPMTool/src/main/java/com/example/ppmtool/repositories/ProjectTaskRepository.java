package com.example.ppmtool.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.ppmtool.domain.ProjectTask;

@Repository
public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {

	List<ProjectTask> findByProjectIdentifierOrderByPriority(String id);
	List<ProjectTask> findByProjectIdentifierOrderByDueDate(String id);
	List<ProjectTask> findByProjectIdentifierOrderByProjectSequence(String id);
	
	ProjectTask findByProjectSequence(String sequence);
}
