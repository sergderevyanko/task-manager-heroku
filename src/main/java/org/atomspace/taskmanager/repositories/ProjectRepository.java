package org.atomspace.taskmanager.repositories;

import org.atomspace.taskmanager.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sergey.derevyanko on 30.07.19.
 */
@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    @Override
    Iterable<Project> findAll();

    Iterable<Project> findAllByProjectLeader(String projectLeader);

    Project findByProjectIdentifier(String projectId);

}
