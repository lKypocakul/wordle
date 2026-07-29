package com.wordle.game;

import com.wordle.dictionary.GameDictionary;
import com.wordle.exceptions.InvalidWordException;
import com.wordle.exceptions.WordNotFoundInDictionaryException;
import com.wordle.util.WordNormalizer;

/**
 * Проверяет введённое игроком (уже нормализованное) слово на общую
 * корректность и на наличие в словаре. Класс не работает с консолью —
 * он либо возвращает нормализованное слово, либо бросает игровое
 * исключение с понятным сообщением, которое печатает главный класс.
 */
public final class InputValidator {

    private final GameDictionary dictionary;

    public InputValidator(GameDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String validate(String normalizedInput) throws InvalidWordException, WordNotFoundInDictionaryException {
        if (normalizedInput.isEmpty()) {
            throw new InvalidWordException("Введено пустое слово.");
        }
        if (normalizedInput.length() != GameDictionary.WORD_LENGTH) {
            throw new InvalidWordException(
                    "Слово должно состоять ровно из " + GameDictionary.WORD_LENGTH
                            + " букв, а введено " + normalizedInput.length() + ".");
        }
        if (!WordNormalizer.isCyrillicOnly(normalizedInput)) {
            throw new InvalidWordException("Слово должно состоять только из русских букв.");
        }
        if (!dictionary.contains(normalizedInput)) {
            throw new WordNotFoundInDictionaryException(normalizedInput);
        }
        return normalizedInput;
    }
}
