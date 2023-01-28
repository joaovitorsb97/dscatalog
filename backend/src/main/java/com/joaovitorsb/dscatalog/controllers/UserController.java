package com.joaovitorsb.dscatalog.controllers;

import com.joaovitorsb.dscatalog.dtos.UserDTO;
import com.joaovitorsb.dscatalog.dtos.UserInsertDTO;
import com.joaovitorsb.dscatalog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping         
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllPaged(pageable));
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }
    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.insert(userDTO));
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userDTO));
    }
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
