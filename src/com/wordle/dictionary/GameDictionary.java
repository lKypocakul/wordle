package com.wordle.dictionary;

import com.wordle.exceptions.DictionaryLoadException;
import com.wordle.exceptions.EmptyDictionaryException;
import com.wordle.logging.GameLogger;
import com.wordle.util.WordNormalizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Итоговый игровой словарь: читает полный файл словаря (в котором слова
 * отсортированы по алфавиту, а не по длине), нормализует их и оставляет
 * только слова нужной длины ({@value #WORD_LENGTH} букв).
 * Класс не работает с консолью напрямую — все диагностические сообщения
 * уходят в переданный лог, а не в System.out.
 */
public final class GameDictionary {

    public static final int WORD_LENGTH = 5;

    private final Set<String> words;
    private final GameLogger logger;

    public GameDictionary(String filePath, GameLogger logger)
            throws DictionaryLoadException, EmptyDictionaryException {
        this.logger = logger;
        this.words = load(filePath);
        if (this.words.isEmpty()) {
            logger.error("Словарь пуст после фильтрации по длине " + WORD_LENGTH, null);
            throw new EmptyDictionaryException(
                    "В словаре не нашлось ни одного слова из " + WORD_LENGTH + " букв.");
        }
        logger.info("Словарь загружен, слов длиной " + WORD_LENGTH + ": " + words.size());
    }

    private Set<String> load(String filePath) throws DictionaryLoadException {
        Set<String> result = new LinkedHashSet<>();
        // try-with-resources гарантированно закроет файл словаря
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int totalLines = 0;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                String normalized = WordNormalizer.normalize(line);
                if (normalized.isEmpty()) {
                    continue;
                }
                if (normalized.length() == WORD_LENGTH && WordNormalizer.isCyrillicOnly(normalized)) {
                    result.add(normalized);
                }
            }
            logger.info("Прочитано строк из файла словаря: " + totalLines);
        } catch (IOException e) {
            throw new DictionaryLoadException("Не удалось прочитать файл словаря: " + filePath, e);
        }
        return result;
    }

    public boolean contains(String normalizedWord) {
        return words.contains(normalizedWord);
    }

    public int size() {
        return words.size();
    }

    public Set<String> words() {
        return Collections.unmodifiableSet(words);
    }

    public String randomWord(Random random) {
        int index = random.nextInt(words.size());
        int i = 0;
        for (String word : words) {
            if (i == index) {
                return word;
            }
            i++;
        }
        // Недостижимо при корректном random.nextInt(words.size())
        throw new IllegalStateException("Не удалось выбрать случайное слово из словаря");
    }
}
