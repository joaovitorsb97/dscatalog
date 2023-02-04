package com.joaovitorsb.dscatalog.services.validation;

import com.joaovitorsb.dscatalog.dtos.UserInsertDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.joaovitorsb.dscatalog.dtos.UserInsertDTO;
import com.joaovitorsb.dscatalog.entities.User;
import com.joaovitorsb.dscatalog.repositories.UserRepository;
import com.joaovitorsb.dscatalog.controllers.exception.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserInsertValid ann) {
    }

    @Override
    public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();
        
        if(userRepository.findByEmail(dto.getEmail()) != null){
            list.add(new FieldMessage("email", "Email already exists"));
        }

        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getFieldMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }
        return list.isEmpty();
    }
}