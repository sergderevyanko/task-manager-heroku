package org.atomspace.taskmanager.services;

import org.atomspace.taskmanager.domain.Backlog;
import org.atomspace.taskmanager.domain.ProjectTask;
import org.atomspace.taskmanager.exceptions.ProjectNotFoundException;
import org.atomspace.taskmanager.repositories.BacklogRepository;
import org.atomspace.taskmanager.repositories.ProjectRepository;
import org.atomspace.taskmanager.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username){
        // Implementation considerations
        // 1. PTs to be added to a specific project, project! = null, BL exists
        // 2. set the bl to pt
        // 3. we want our project sequence to be like this: IDPRO-1, IDPRO-2 ... IDPRO-100
        // 4. Update BL SEQUENCE
        Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();

        checkNotNullBacklog(backlog, projectIdentifier);

        projectTask.setBacklog(backlog);
        Integer backlogSequence = backlog.getPTSequence() + 1;
        backlog.setPTSequence(backlogSequence);

        projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + backlogSequence);
        projectTask.setProjectIdentifier(backlog.getProjectIdentifier());

        //INITIAL priority when priority is null
        if(projectTask.getPriority() == null || projectTask.getPriority() == 0 ){
            //replace 3 with constant or enum
            projectTask.setPriority(3);
        }
        //INITIAL status when priority is null
        if(projectTask.getStatus() == null || projectTask.getStatus().isEmpty()){
            //replace with Enum
            projectTask.setStatus("TO_DO");
        }
        backlogRepository.save(backlog);
        return projectTaskRepository.save(projectTask);
    }

    public List<ProjectTask> findBacklogById(String projectIdentifier, String username) {
        if(projectRepository.findByProjectIdentifier(projectIdentifier) == null ){
            throw new ProjectNotFoundException("Project " + projectIdentifier + " not found");
        }
        projectService.findProjectByIdentifier(projectIdentifier, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectIdentifier);
    }

    public ProjectTask findPTByProjectSequence(String backlogId, String projectSequence, String username){

        //make sure we are searching on an existing project
        Backlog backlog = projectService.findProjectByIdentifier(backlogId, username).getBacklog();
        checkNotNullBacklog(backlog, backlogId);
        //make sure out task exist
        ProjectTask projectTask = projectTaskRepository.findProjectTaskByProjectSequence(projectSequence);
        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task " + projectSequence + " not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        //TODO: Think of making this better
        if(!projectTask.getProjectIdentifier().equals(backlogId)){
            throw new ProjectNotFoundException("Project Task " + projectSequence + " does not exist in project " +
                    backlogId);
        }
        return projectTask;
    }

    public ProjectTask updateProjectTaskBySequence(ProjectTask updatedTask, String backlogId,
                                                   String projectSequence, String username){

        ProjectTask projectTask = findPTByProjectSequence(backlogId, projectSequence, username);
        //really? that's not safe
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deleteProjectTaskBySequence(String backlogId, String projectSequence, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlogId, projectSequence, username);
        projectTaskRepository.delete(projectTask);
    }


    //TODO: Think about more generic approach
    private static void checkNotNullBacklog(Backlog backlog, String projectIdentifier){
        if(backlog == null){
            throw new ProjectNotFoundException("Project " + projectIdentifier + " not found");
        }
    }
}
