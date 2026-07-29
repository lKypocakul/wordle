package com.wordle.exceptions;

/**
 * Базовый класс для исключений, связанных с игровыми ситуациями
 * (некорректный ввод игрока, слово не найдено в словаре и т.д.).
 * Такие исключения перехватываются в игровом цикле и превращаются
 * в понятное сообщение для игрока — они не прерывают игру.
 */
public class GameLogicException extends Exception {

    public GameLogicException(String message) {
        super(message);
    }
}
