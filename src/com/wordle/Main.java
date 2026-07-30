package com.wordle;

import com.wordle.dictionary.GameDictionary;
import com.wordle.exceptions.GameLogicException;
import com.wordle.exceptions.GameProgramException;
import com.wordle.exceptions.InternalGameStateException;
import com.wordle.exceptions.WordNotFoundInDictionaryException;
import com.wordle.game.GameState;
import com.wordle.game.HintEngine;
import com.wordle.game.InputValidator;
import com.wordle.game.WordEvaluator;
import com.wordle.logging.GameLogger;
import com.wordle.util.WordNormalizer;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public final class Main {

    private static final String DICTIONARY_PATH = "resources/dictionary.txt";
    private static final String LOG_PATH = "wordle.log";
    private static final int TOTAL_ATTEMPTS = 6;

    public static void main(String[] args) {
        try (GameLogger logger = new GameLogger(LOG_PATH)) {
            try {
                runGame(logger);
            } catch (GameProgramException e) {
                logger.error("Фатальная ошибка программы", e);
                System.out.println("Не удалось запустить игру: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Непредвиденная ошибка", e);
                System.out.println("Произошла непредвиденная ошибка, игра остановлена. "
                        + "Подробности записаны в лог.");
            }
        } catch (GameProgramException e) {
            System.out.println("Не удалось инициализировать лог-файл: " + e.getMessage());
        }
    }

    private static void runGame(GameLogger logger) throws GameProgramException {
        GameDictionary dictionary = new GameDictionary(DICTIONARY_PATH, logger);
        InputValidator validator = new InputValidator(dictionary);
        HintEngine hintEngine = new HintEngine(dictionary, logger);
        Random random = new Random();

        String secretWord = dictionary.randomWord(random);
        GameState state = new GameState(secretWord, TOTAL_ATTEMPTS, logger);

        printWelcome();

        try (Scanner scanner = new Scanner(System.in)) {
            while (!state.isFinished()) {
                System.out.println();
                System.out.println("Осталось попыток: " + state.getAttemptsLeft()
                        + ". Введите слово из " + GameDictionary.WORD_LENGTH
                        + " букв (или просто нажмите Enter для подсказки):");
                String rawInput = scanner.nextLine();
                String normalized = WordNormalizer.normalize(rawInput);

                String guess;
                if (normalized.isEmpty()) {
                    guess = giveHint(state, hintEngine, random, logger);
                    if (guess == null) {
                        continue;
                    }
                } else {
                    guess = tryValidate(normalized, validator, logger);
                    if (guess == null) {
                        continue;
                    }
                }

                boolean directHit = guess.equals(secretWord);

                String feedback = state.registerGuess(guess);
                System.out.println(guess);
                System.out.println(feedback);

                if (directHit && !WordEvaluator.isWin(feedback)) {
                    throw new InternalGameStateException(
                            "Слово совпало с ответом, но обратная связь не 'победная': " + feedback);
                }
            }
        }

        printResult(state);
    }

    private static String tryValidate(String normalized, InputValidator validator, GameLogger logger) {
        try {
            return validator.validate(normalized);
        } catch (GameLogicException e) {
            logger.info("Отклонён ввод игрока: " + e.getMessage());
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static String giveHint(GameState state, HintEngine hintEngine, Random random, GameLogger logger) {
        try {
            String suggestion = hintEngine.suggestWord(state, random);
            System.out.println("Подсказка: " + suggestion);
            return suggestion;
        } catch (WordNotFoundInDictionaryException e) {
            logger.info("Не удалось выдать подсказку: " + e.getMessage());
            System.out.println("Подходящих слов для подсказки не найдено, попробуйте ввести своё слово.");
            return null;
        }
    }

    private static void printWelcome() {
        System.out.println("Добро пожаловать в игру Wordle!");
        System.out.println("Компьютер загадал существительное из " + GameDictionary.WORD_LENGTH + " букв.");
        System.out.println("У вас " + TOTAL_ATTEMPTS + " попыток.");
        System.out.println("После каждой попытки вы увидите подсказку из символов:");
        System.out.println("  " + WordEvaluator.CORRECT_POSITION + " — буква на своём месте");
        System.out.println("  " + WordEvaluator.WRONG_POSITION + " — буква есть в слове, но не на этом месте");
        System.out.println("  " + WordEvaluator.ABSENT + " — такой буквы в слове нет");
    }

    private static void printResult(GameState state) {
        List<com.wordle.game.GuessRecord> history = state.getHistory();
        System.out.println();
        if (state.isWon()) {
            System.out.println("Поздравляем, вы угадали слово \"" + state.getSecretWord()
                    + "\" за " + history.size() + " попыток(ки)!");
        } else {
            System.out.println("Попытки закончились. Загаданное слово было: \"" + state.getSecretWord() + "\".");
        }
    }
}