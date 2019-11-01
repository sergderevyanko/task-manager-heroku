package org.atomspace.taskmanager.repositories;

import org.atomspace.taskmanager.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    User getById(Long id);
}
