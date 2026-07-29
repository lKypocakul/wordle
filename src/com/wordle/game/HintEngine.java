package com.wordle.game;

import com.wordle.dictionary.GameDictionary;
import com.wordle.exceptions.WordNotFoundInDictionaryException;
import com.wordle.logging.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Вычисляет множество слов словаря, которые ещё могут быть ответом,
 * учитывая все ранее полученные подсказки, и предлагает игроку одно
 * из них. Класс не работает с консолью.
 */
public final class HintEngine {

    private final GameDictionary dictionary;
    private final GameLogger logger;

    public HintEngine(GameDictionary dictionary, GameLogger logger) {
        this.dictionary = dictionary;
        this.logger = logger;
    }

    /**
     * Возвращает список слов словаря, которые согласуются со всеми
     * известными на данный момент ограничениями (известные позиции,
     * буквы не на своих местах, отсутствующие буквы), и которые ещё
     * не были использованы как ход или как ранее выданная подсказка.
     */
    public List<String> findCandidates(GameState state) {
        char[] known = state.getKnownPositions();
        Map<Character, Set<Integer>> presentWrongPos = state.getPresentButWrongPosition();
        Set<Character> absent = state.getAbsentLetters();

        List<String> candidates = new ArrayList<>();
        for (String word : dictionary.words()) {
            if (state.wasAlreadyGuessed(word) || state.getSuggestedWords().contains(word)) {
                continue;
            }
            if (matches(word, known, presentWrongPos, absent)) {
                candidates.add(word);
            }
        }
        return candidates;
    }

    private boolean matches(String word, char[] known,
                             Map<Character, Set<Integer>> presentWrongPos,
                             Set<Character> absent) {
        // Проверка известных позиций ('+').
        for (int i = 0; i < known.length; i++) {
            if (known[i] != '\0' && word.charAt(i) != known[i]) {
                return false;
            }
        }
        // Проверка отсутствующих букв ('-'), с учётом того, что такая
        // буква может всё же быть в слове, если по ней уже есть
        // положительный сигнал где-то ещё (обрабатывается в GameState,
        // здесь absent содержит только буквы без положительных сигналов).
        for (char letter : absent) {
            if (word.indexOf(letter) >= 0) {
                return false;
            }
        }
        // Проверка букв, которые есть в слове, но не на этих позициях ('^').
        for (Map.Entry<Character, Set<Integer>> entry : presentWrongPos.entrySet()) {
            char letter = entry.getKey();
            if (word.indexOf(letter) < 0) {
                return false;
            }
            for (int forbiddenPos : entry.getValue()) {
                if (word.charAt(forbiddenPos) == letter) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Предлагает игроку одно случайное подходящее слово из словаря.
     * Гарантированно не должно возвращать пустой список, если ограничения
     * вычислены на основе подсказок для реального загаданного слова —
     * само загаданное слово всегда останется среди кандидатов, пока оно
     * ещё не было угадано.
     */
    public String suggestWord(GameState state, Random random) throws WordNotFoundInDictionaryException {
        List<String> candidates = findCandidates(state);
        if (candidates.isEmpty()) {
            // Такая ситуация не должна возникать при корректной логике
            // ограничений, поэтому дополнительно логируем как внутреннюю
            // проблему, но игроку сообщаем через обычное игровое исключение.
            logger.error("Не найдено ни одного подходящего слова для подсказки", null);
            throw new WordNotFoundInDictionaryException("(нет подходящих слов для подсказки)");
        }
        String suggestion = candidates.get(random.nextInt(candidates.size()));
        state.rememberSuggestion(suggestion);
        logger.info("Выдана подсказка: " + suggestion + " (кандидатов было: " + candidates.size() + ")");
        return suggestion;
    }
}
