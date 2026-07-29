package com.wordle.exceptions;

/**
 * Бросается, когда введённая игроком строка не проходит базовую
 * проверку на корректность (пустая строка, неверная длина,
 * недопустимые символы и т.п.) ещё до обращения к словарю.
 */
public class InvalidWordException extends GameLogicException {

    public InvalidWordException(String message) {
        super(message);
    }
}
