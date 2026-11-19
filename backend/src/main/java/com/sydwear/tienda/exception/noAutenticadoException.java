package com.sydwear.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class noAutenticadoException extends ResponseStatusException {

    public noAutenticadoException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}