package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConflitoDatasException extends IllegalArgumentException {
    public ConflitoDatasException(String message) {
        super(message);
    }
}
