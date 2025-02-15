package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UsuarioSemAutorizacaoException extends RuntimeException {
    public UsuarioSemAutorizacaoException(String message) {
        super(message);
    }
}
