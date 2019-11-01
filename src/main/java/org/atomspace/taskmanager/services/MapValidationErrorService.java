package org.atomspace.taskmanager.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey.derevyanko on 30.07.19.
 */
@Service
public class MapValidationErrorService {

    public ResponseEntity<?> mapValidation(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return new ResponseEntity<Map>(errorMap, HttpStatus.BAD_REQUEST);
        }
        else {
            return null;
        }
    }
}
