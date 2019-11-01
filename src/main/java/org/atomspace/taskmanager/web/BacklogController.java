package org.atomspace.taskmanager.web;

import org.atomspace.taskmanager.domain.ProjectTask;
import org.atomspace.taskmanager.services.MapValidationErrorService;
import org.atomspace.taskmanager.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{backlog_id}")
    public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                                     @PathVariable String backlog_id, Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);
        if(errorMap != null) return errorMap;

        ProjectTask storedTask = projectTaskService.addProjectTask(backlog_id, projectTask, principal.getName());
        return new ResponseEntity<ProjectTask>(storedTask, HttpStatus.CREATED);
    }

    @GetMapping("/{backlog_id}")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable(name = "backlog_id") String backlogId,
                                                   Principal principal){
        return projectTaskService.findBacklogById(backlogId, principal.getName());
    }

    @GetMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> getProjectTask(@PathVariable(name = "backlog_id") String backlogId,
                                            @PathVariable(name = "pt_id") String ptId,
                                            Principal principal) {
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask,
                                               BindingResult result,
                                               @PathVariable(name = "backlog_id") String backlogId,
                                               @PathVariable(name = "pt_id") String ptId,
                                               Principal principal) {
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidation(result);
        if(errorMap != null) return errorMap;
        ProjectTask updatedTask = projectTaskService.updateProjectTaskBySequence(
                projectTask, backlogId, ptId, principal.getName());

        return new ResponseEntity<ProjectTask>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable(name = "backlog_id") String backlogId,
                                               @PathVariable(name = "pt_id") String ptId,
                                               Principal principal) {
        projectTaskService.deleteProjectTaskBySequence(backlogId, ptId, principal.getName());

        return new ResponseEntity<String>("Project Task " + ptId + " was deleted", HttpStatus.OK);
    }

}
