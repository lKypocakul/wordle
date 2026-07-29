package com.wordle.exceptions;

/**
 * Бросается, когда после чтения и фильтрации словаря не осталось
 * ни одного подходящего пятибуквенного слова.
 */
public class EmptyDictionaryException extends GameProgramException {

    public EmptyDictionaryException(String message) {
        super(message);
    }
}
