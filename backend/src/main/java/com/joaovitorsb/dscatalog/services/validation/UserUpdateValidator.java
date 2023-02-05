package com.joaovitorsb.dscatalog.services.validation;

import com.joaovitorsb.dscatalog.controllers.exception.FieldMessage;
import com.joaovitorsb.dscatalog.dtos.UserInsertDTO;
import com.joaovitorsb.dscatalog.dtos.UserUpdateDTO;
import com.joaovitorsb.dscatalog.entities.User;
import com.joaovitorsb.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {


    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserUpdateValid ann) {
    }

    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        @SuppressWarnings("unchecked") //Suppress the yellow warning
        var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE); //Get all attributes from URL
        Long userId = Long.parseLong(uriVars.get("id")); //Access id from user/{id} request

        User user = userRepository.findByEmail(dto.getEmail());

        if(user != null && !userId.equals(user.getId())){
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