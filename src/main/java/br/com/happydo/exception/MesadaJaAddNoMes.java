package br.com.happydo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MesadaJaAddNoMes extends RuntimeException {
    public MesadaJaAddNoMes(String message) {
        super(message);
    }
}
