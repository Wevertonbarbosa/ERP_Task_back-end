package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsuarioNaoPodeSerExcluidoException extends RuntimeException {
    public UsuarioNaoPodeSerExcluidoException(String message) {
        super(message);
    }
}
