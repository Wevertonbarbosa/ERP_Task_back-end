package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PontuacaoInvalidaException extends RuntimeException {
    public PontuacaoInvalidaException(String message) {
        super(message);
    }
}
