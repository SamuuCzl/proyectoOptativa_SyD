package com.sydwear.tienda.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class peticionIncorrectaException extends ResponseStatusException {
    public peticionIncorrectaException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }
}
