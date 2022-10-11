package com.joaovitorsb.dscatalog.services.exceptions;


public class ResourceNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(Object id){
        super("Entity not found: " + id);
    }

}
