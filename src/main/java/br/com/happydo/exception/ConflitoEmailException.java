package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitoEmailException extends RuntimeException {
    public ConflitoEmailException(String message) {
        super(message);
    }
}
