package com.example.ppmtool.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ppmtool.domain.Backlog;
import com.example.ppmtool.domain.Project;
import com.example.ppmtool.domain.ProjectTask;
import com.example.ppmtool.exceptions.ProjectNotFoundException;
import com.example.ppmtool.repositories.BacklogRepository;
import com.example.ppmtool.repositories.ProjectRepository;
import com.example.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {

	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		//Exceptions: creating tasks for project that does not exist
		try
		{
			//Add PTs to specific project, project exists and thus backlog exists
			Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
			//Set BL to PT
			projectTask.setBacklog(backlog);
			//Proj seq like IDPRO-1 IDPRO-2
			Integer BacklogSequence = backlog.getPTSequence();
			//Update BL seq
			BacklogSequence++;
			backlog.setPTSequence(BacklogSequence);
			//Add seq to PT
			projectTask.setProjectSequence(projectIdentifier+"-"+BacklogSequence);
			projectTask.setProjectIdentifier(projectIdentifier);
			
			//Initial priority and status when they are null
			if(projectTask.getPriority()==0||projectTask.getPriority()==null)
			{
				projectTask.setPriority(3);
			}
			if(projectTask.getStatus()==""||projectTask.getStatus()==null)
			{
				projectTask.setStatus("TO_DO");
			}
		}
		catch(Exception e)
		{
			throw new ProjectNotFoundException("Project not found");
		}
		
		return projectTaskRepository.save(projectTask);
	}
	
	public Iterable<ProjectTask>findBacklogById(String id){
		
		Project project = projectRepository.findByProjectIdentifier(id);
		if(project==null)
		{
			throw new ProjectNotFoundException("Project with id "+id+" does not exist");
		}
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
	}
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id)
	{
		//make sure we are searching on the correct backlog
		Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
		if(backlog==null)
		{
			throw new ProjectNotFoundException("Project with id "+backlog_id+" does not exist");
		}
		//make sure task exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask==null)
		{
			throw new ProjectNotFoundException("Project Task "+pt_id+" not found");
		}
		//make sure backlog/proj id in path matches to right proj
		if(!projectTask.getProjectIdentifier().equals(backlog_id))
		{
			throw new ProjectNotFoundException("Project Task "+pt_id+" does not exist in Project "+backlog_id);
		} 
		return projectTask;
	}
	
	//Update Project Task
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		projectTask = updatedTask;
		return projectTaskRepository.save(projectTask);
	}
	
	//Delete Project Task
	public void deletePTBySequence(String backlog_id, String pt_id)
	{
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
		projectTaskRepository.delete(projectTask);
	}
	
}
