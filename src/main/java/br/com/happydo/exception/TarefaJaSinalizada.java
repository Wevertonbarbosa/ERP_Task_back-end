package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TarefaJaSinalizada extends RuntimeException {
    public TarefaJaSinalizada(String message) {
        super(message);
    }
}