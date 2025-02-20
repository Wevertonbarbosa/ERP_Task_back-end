package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MesadaSemPermissaoException extends RuntimeException {
    public MesadaSemPermissaoException(String message) {
        super(message);
    }
}
