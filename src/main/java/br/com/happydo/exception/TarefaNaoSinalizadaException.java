package br.com.happydo.exception;

public class TarefaNaoSinalizadaException extends IllegalStateException {
    public TarefaNaoSinalizadaException(String message) {
        super(message);
    }
}
