package com.wordle.game;

import com.wordle.exceptions.InternalGameStateException;
import com.wordle.logging.GameLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Хранит текущее состояние одной партии: загаданное слово, число
 * оставшихся попыток, историю ходов и накопленные подсказки-ограничения
 * (какие буквы точно на своих местах, какие есть, но не там, какие
 * отсутствуют). Эти ограничения используются {@link HintEngine} для
 * поиска подходящих слов. Класс не работает с консолью.
 */
public final class GameState {

    private final String secretWord;
    private final int totalAttempts;
    private final GameLogger logger;

    private int attemptsLeft;
    private final List<GuessRecord> history = new ArrayList<>();
    private final Set<String> usedWords = new HashSet<>();
    private final Set<String> suggestedWords = new HashSet<>();

    // Ограничения, извлечённые из подсказок:
    private final char[] knownPositions; // '\0', если позиция ещё не известна
    private final Map<Character, Set<Integer>> presentButWrongPosition = new HashMap<>();
    private final Set<Character> absentLetters = new HashSet<>();

    private boolean finished = false;
    private boolean won = false;

    public GameState(String secretWord, int totalAttempts, GameLogger logger) {
        this.secretWord = secretWord;
        this.totalAttempts = totalAttempts;
        this.attemptsLeft = totalAttempts;
        this.logger = logger;
        this.knownPositions = new char[secretWord.length()];
        logger.state("Новая игра начата, попыток: " + totalAttempts);
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public List<GuessRecord> getHistory() {
        return history;
    }

    public boolean wasAlreadyGuessed(String word) {
        return usedWords.contains(word);
    }

    /**
     * Регистрирует очередную (уже проверенную по словарю) попытку игрока,
     * обновляет ограничения для подсказок и уменьшает число оставшихся попыток.
     */
    public String registerGuess(String guess) {
        if (finished) {
            throw new InternalGameStateException(
                    "Попытка сделать ход в уже завершённой игре");
        }
        if (attemptsLeft <= 0) {
            throw new InternalGameStateException(
                    "Попытка сделать ход при нулевом числе оставшихся попыток");
        }

        String feedback = WordEvaluator.evaluate(secretWord, guess);
        history.add(new GuessRecord(guess, feedback));
        usedWords.add(guess);
        updateConstraints(guess, feedback);
        attemptsLeft--;

        logger.state("Ход: " + guess + " -> " + feedback + ", осталось попыток: " + attemptsLeft);

        if (WordEvaluator.isWin(feedback)) {
            won = true;
            finished = true;
            logger.state("Игра завершена: победа");
        } else if (attemptsLeft == 0) {
            finished = true;
            logger.state("Игра завершена: попытки закончились");
        }
        return feedback;
    }

    private void updateConstraints(String guess, String feedback) {
        for (int i = 0; i < feedback.length(); i++) {
            char mark = feedback.charAt(i);
            char letter = guess.charAt(i);
            switch (mark) {
                case WordEvaluator.CORRECT_POSITION:
                    knownPositions[i] = letter;
                    break;
                case WordEvaluator.WRONG_POSITION:
                    presentButWrongPosition
                            .computeIfAbsent(letter, k -> new HashSet<>())
                            .add(i);
                    break;
                case WordEvaluator.ABSENT:
                    // Буква может встречаться в слове и быть отмечена как
                    // отсутствующая на "лишних" повторах — добавляем в
                    // absentLetters только если по ней нет положительных
                    // сигналов ('+' или '^') в этом же ходе, иначе просто
                    // игнорируем, чтобы не потерять решение.
                    if (!letterHasPositiveSignal(guess, feedback, letter)) {
                        absentLetters.add(letter);
                    }
                    break;
                default:
                    throw new InternalGameStateException("Неизвестный символ подсказки: " + mark);
            }
        }
    }

    private boolean letterHasPositiveSignal(String guess, String feedback, char letter) {
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == letter
                    && (feedback.charAt(i) == WordEvaluator.CORRECT_POSITION
                        || feedback.charAt(i) == WordEvaluator.WRONG_POSITION)) {
                return true;
            }
        }
        return false;
    }

    public char[] getKnownPositions() {
        return knownPositions;
    }

    public Map<Character, Set<Integer>> getPresentButWrongPosition() {
        return presentButWrongPosition;
    }

    public Set<Character> getAbsentLetters() {
        return absentLetters;
    }

    public void rememberSuggestion(String word) {
        suggestedWords.add(word);
    }

    public Set<String> getSuggestedWords() {
        return suggestedWords;
    }

    public GameLogger getLogger() {
        return logger;
    }
}
