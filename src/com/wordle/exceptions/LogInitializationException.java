package com.wordle.exceptions;

/**
 * Бросается, если не удалось создать или открыть лог-файл программы.
 */
public class LogInitializationException extends GameProgramException {

    public LogInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
