package org.atomspace.taskmanager.services;

import org.atomspace.taskmanager.domain.Backlog;
import org.atomspace.taskmanager.domain.Project;
import org.atomspace.taskmanager.domain.User;
import org.atomspace.taskmanager.exceptions.ProjectIdException;
import org.atomspace.taskmanager.exceptions.ProjectNotFoundException;
import org.atomspace.taskmanager.repositories.BacklogRepository;
import org.atomspace.taskmanager.repositories.ProjectRepository;
import org.atomspace.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sergey.derevyanko on 30.07.19.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;
    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project, String username){

        if(project.getId() != null){
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
            if(existingProject != null && !existingProject.getProjectLeader().equals(username)){
                throw new ProjectNotFoundException("Project not found in your account");
            }else if( existingProject == null) {
                throw new ProjectNotFoundException("Project with ID " + project.getProjectIdentifier() +
                        " does not exist");
            }
        }

        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(username);
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            if(project.getId() == null){
                Backlog backlog = new Backlog();
                backlog.setProject(project);
                project.setBacklog(backlog);
                backlog.setProjectIdentifier(project.getProjectIdentifier());
            }
            else {
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier()));
            }
            return projectRepository.save(project);
        }catch (Exception e){
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier() + "'");
        }
    }

    public Project findProjectByIdentifier(String projectId, String username){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());
        if (project == null){
            throw new ProjectIdException("Project ID '" + projectId + "' does not exist");
        }
        if(!project.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project not found in your account");
        }
        return project;
    }

    public Iterable<Project> findAllProjects(String username){
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username){
        Project project = findProjectByIdentifier(projectId.toUpperCase(), username);
        projectRepository.delete(project);
    }

}
