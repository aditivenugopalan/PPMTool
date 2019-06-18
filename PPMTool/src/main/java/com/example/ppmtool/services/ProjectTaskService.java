package com.example.ppmtool.services;

import java.security.Principal;

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
	
	@Autowired
	private ProjectService projectService;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
			//Add PTs to specific project, project exists and thus backlog exists
			Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();// backlogRepository.findByProjectIdentifier(projectIdentifier);
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
			if(projectTask.getPriority()==null||projectTask.getPriority()==0)
			{
				projectTask.setPriority(3);
			}
			if(projectTask.getStatus()==""||projectTask.getStatus()==null)
			{
				projectTask.setStatus("TO_DO");
			}
		
		return projectTaskRepository.save(projectTask);
	}
	
	public Iterable<ProjectTask>findBacklogById(String sortBy, String id, String username){
		
		projectService.findProjectByIdentifier(id,username);
		if(sortBy.equals("priority")) return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
		else if(sortBy.equals("duedate")) return projectTaskRepository.findByProjectIdentifierOrderByDueDate(id);
		else return projectTaskRepository.findByProjectIdentifierOrderByProjectSequence(id);
	}
	
	public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id, String username)
	{
		//make sure we are searching on the correct backlog
		projectService.findProjectByIdentifier(backlog_id,username);
		
		//make sure task exists
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
		if(projectTask==null)
		{
			throw new ProjectNotFoundException("Project Task "+pt_id+" not found");
		}
		
		//make sure backlog/project id in path matches to right project
		if(!projectTask.getProjectIdentifier().equals(backlog_id))
		{
			throw new ProjectNotFoundException("Project Task "+pt_id+" does not exist in Project "+backlog_id);
		} 
		return projectTask;
	}
	
	//Update Project Task
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username) {
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		projectTask = updatedTask;
		return projectTaskRepository.save(projectTask);
	}
	
	//Delete Project Task
	public void deletePTBySequence(String backlog_id, String pt_id, String username)
	{
		ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id, username);
		projectTaskRepository.delete(projectTask);
	}
	
}
