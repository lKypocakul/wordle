package com.wordle.exceptions;

/**
 * Непроверяемое исключение для внутренних ошибок программы —
 * ситуаций, которые в теории не должны возникать при корректной
 * работе алгоритмов игры (потеря решения, отрицательное число
 * попыток, обращение к завершённой игре и т.п.). Наличие такой
 * ошибки говорит об ошибке в логике программы, а не в действиях игрока.
 */
public class InternalGameStateException extends RuntimeException {

    public InternalGameStateException(String message) {
        super(message);
    }
}
