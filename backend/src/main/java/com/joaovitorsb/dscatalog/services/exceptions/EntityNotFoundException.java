package com.joaovitorsb.dscatalog.services.exceptions;


public class EntityNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(Object id){
        super("Entity not found: " + id);
    }

}
