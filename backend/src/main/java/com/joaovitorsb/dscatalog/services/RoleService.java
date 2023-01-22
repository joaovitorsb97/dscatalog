package com.joaovitorsb.dscatalog.services;

import com.joaovitorsb.dscatalog.dtos.RoleDTO;
import com.joaovitorsb.dscatalog.entities.Role;
import com.joaovitorsb.dscatalog.repositories.RoleRepository;
import com.joaovitorsb.dscatalog.repositories.RoleRepository;
import com.joaovitorsb.dscatalog.services.exceptions.DatabaseException;
import com.joaovitorsb.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Transactional(readOnly = true)
    public Page<RoleDTO> findAllPaged(Pageable pageable) {
       Page<Role> page = roleRepository.findAll(pageable);
       return page.map(RoleDTO::new);
    }
    @Transactional(readOnly = true)
    public RoleDTO findById(Long id){
        Optional<Role> obj = roleRepository.findById(id);
        return obj.stream().map(RoleDTO::new).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));
    }
    @Transactional
    public RoleDTO insert(RoleDTO categoryDTO) {
        Role category = new Role();
        category.setAuthority(categoryDTO.getAuthority());
        return new RoleDTO(roleRepository.save(category));
    }
    @Transactional
    public RoleDTO update(Long id, RoleDTO categoryDTO) {
        try{
            Role role = roleRepository.getById(id);
            role.setAuthority(categoryDTO.getAuthority());
            return new RoleDTO(roleRepository.save(role));
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }
    }
    public void delete(Long id) {
        try{
            roleRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id " + id + " not found");
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException(e.getMessage());
        }
    }

}
