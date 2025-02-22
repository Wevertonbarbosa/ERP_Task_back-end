package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GastoNaoEncontradoException extends RuntimeException {
    public GastoNaoEncontradoException(String message) {
        super(message);
    }
}
