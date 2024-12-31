package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TarefaNaoSinalizadaException extends IllegalStateException {
    public TarefaNaoSinalizadaException(String message) {
        super(message);
    }
}
