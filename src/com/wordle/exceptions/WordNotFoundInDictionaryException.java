package com.wordle.exceptions;

/**
 * Бросается, когда введённое игроком слово отсутствует в игровом словаре.
 */
public class WordNotFoundInDictionaryException extends GameLogicException {

    public WordNotFoundInDictionaryException(String word) {
        super("Слово \"" + word + "\" отсутствует в словаре игры.");
    }
}
