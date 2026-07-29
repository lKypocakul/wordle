package com.wordle.exceptions;

/**
 * Базовый класс для ошибок, связанных с работой самой программы
 * (файл словаря, лог-файл и т.п.), а не с игровой ситуацией.
 * Такие ошибки являются фатальными для запуска/работы игры.
 */
public class GameProgramException extends Exception {

    public GameProgramException(String message) {
        super(message);
    }

    public GameProgramException(String message, Throwable cause) {
        super(message, cause);
    }
}
