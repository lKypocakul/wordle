package com.wordle.exceptions;

/**
 * Бросается, когда файл словаря не найден или не может быть прочитан.
 */
public class DictionaryLoadException extends GameProgramException {

    public DictionaryLoadException(String message) {
        super(message);
    }

    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
