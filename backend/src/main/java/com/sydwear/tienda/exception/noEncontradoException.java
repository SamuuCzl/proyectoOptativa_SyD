package com.sydwear.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class noEncontradoException extends ResponseStatusException {

    public noEncontradoException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}