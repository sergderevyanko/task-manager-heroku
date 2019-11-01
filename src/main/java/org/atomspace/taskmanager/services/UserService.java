package org.atomspace.taskmanager.services;

import org.atomspace.taskmanager.domain.User;
import org.atomspace.taskmanager.exceptions.UsernameAlreadyExistsException;
import org.atomspace.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(User newUser){
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            // according to course that magic causes an SQL exception, but AFAIU it is not required
            // newUser.setUsername(newUser.getUsername());

            // Make sure that password and confirmPassword match
            // Don't persist or show password or/and confirm password
            return userRepository.save(newUser);
        }
        catch (Exception exception){
            throw new UsernameAlreadyExistsException("Username '" + newUser.getUsername() + "' already exists");
        }
    }

}
