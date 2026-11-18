package com.sydwear.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class conflictoException extends ResponseStatusException {

    public conflictoException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}