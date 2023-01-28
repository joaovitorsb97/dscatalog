package com.joaovitorsb.dscatalog.controllers;

import com.joaovitorsb.dscatalog.dtos.RoleDTO;
import com.joaovitorsb.dscatalog.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<Page<RoleDTO>> findAll(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.findAllPaged(pageable));
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.findById(id));
    }
    @PostMapping
    public ResponseEntity<RoleDTO> insert(@RequestBody RoleDTO roleDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.insert(roleDTO));
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable Long id, @RequestBody RoleDTO roleDTO){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.update(id, roleDTO));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        roleService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
