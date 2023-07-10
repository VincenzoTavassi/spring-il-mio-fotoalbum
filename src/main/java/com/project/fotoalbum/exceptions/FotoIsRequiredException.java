package com.project.fotoalbum.exceptions;

public class FotoIsRequiredException extends RuntimeException{

    public FotoIsRequiredException(String message) {
        super(message);
    }
}
