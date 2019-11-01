package org.atomspace.taskmanager.utils;

import org.atomspace.taskmanager.domain.Project;
import org.atomspace.taskmanager.domain.ProjectTask;
import org.atomspace.taskmanager.domain.User;
import org.atomspace.taskmanager.repositories.ProjectRepository;
import org.atomspace.taskmanager.services.ProjectService;
import org.atomspace.taskmanager.services.ProjectTaskService;
import org.atomspace.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final ProjectService projectService;
    private final static Map<Integer, String> statusMap =  new HashMap<>();
    private Random random = new Random();

    static {
        statusMap.put(1, "TO_DO");
        statusMap.put(2, "IN_PROGRESS");
        statusMap.put(3, "DONE");
    }

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public DatabaseLoader(ProjectService projectService) {
        this.projectService = projectService;
    }



    @Override
    public void run(String... strings) throws Exception {
        if(projectRepository.count() > 0){
            return;
        }

        List<Project> projects = new ArrayList<>();
        projects.add(new Project("Spring Hackaton", "ASSH", "A 3-days long " +
                "project aimed on building prod ready project"));
        projects.add(new Project("React Intro", "ASRI", "A project " +
                "aimed on getting hands-on experience with React and other tools"));
        projects.add(new Project("Java Guru", "ASJG", "A project " +
                "that help people learn how to build a complex systems with JAVA techological stack"));
        User user1 = createUser("atomuser@gmail.com");
        User user2 = createUser("testuser@gmail.com");
        User user = user1;
        for(Project project: projects){
            if(user == user1){
                user = user2;
            }
            else{
                user = user1;
            }
            this.projectService.saveOrUpdateProject(project, user.getUsername());
            for(int i=0; i<6; i++){
                generateProjectTask(project.getProjectIdentifier(), user.getUsername());
            }
        }
    }

    private User createUser(String username){
        User user = new User();
        user.setUsername(username);
        user.setPassword("atompassword");
        user.setFullName("Atom Space Resident");
        return userService.saveUser(user);
    }

    private void generateProjectTask(String projectIdentifier, String username){
        ProjectTask taskToAdd = new ProjectTask();
        taskToAdd.setSummary("A Task for project: " + projectIdentifier);
        taskToAdd.setPriority(random.nextInt(3) +1);
        taskToAdd.setStatus(statusMap.get(random.nextInt(3) +1));
        projectTaskService.addProjectTask(projectIdentifier, taskToAdd, username);
    }
}