package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.*;
import com.joaovitorsb.dscatalog.entities.Category;
import com.joaovitorsb.dscatalog.entities.Role;
import com.joaovitorsb.dscatalog.entities.User;
import com.joaovitorsb.dscatalog.repositories.UserRepository;
import com.joaovitorsb.dscatalog.repositories.RoleRepository;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> page = userRepository.findAll(pageable);
        return page.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        Optional<User> obj = userRepository.findById(id);
        return obj.stream().map(UserDTO::new).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public UserDTO insert(UserInsertDTO userDTO){
        User user = new User();
        copyDtoToEntity(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return new UserDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO userDTO){
        try{
            User user = userRepository.getById(id);
            copyDtoToEntity(userDTO, user);
            return new UserDTO(userRepository.save(user));
        }catch(EntityNotFoundException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }
    }

    @Transactional
    public void delete(Long id){
        try{
            userRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }catch(DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity){
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();

        for(RoleDTO roleDTO : dto.getRoles()){
            Role role = roleRepository.getById(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if(user == null){
            logger.error("Email username not found: " + username);
            throw new UsernameNotFoundException("Username not found!");
        }
        logger.info("Email username founded: " + username);
        return user;
    }
}
