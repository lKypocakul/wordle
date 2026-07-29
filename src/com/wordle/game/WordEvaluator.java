package com.wordle.game;

import com.wordle.exceptions.InternalGameStateException;

/**
 * Реализует алгоритм сравнения слова, введённого игроком, с загаданным
 * словом по правилам Wordle. Класс не работает с консолью.
 */
public final class WordEvaluator {

    public static final char CORRECT_POSITION = '+';
    public static final char WRONG_POSITION = '^';
    public static final char ABSENT = '-';

    private WordEvaluator() {
    }

    /**
     * Сравнивает введённое слово с загаданным и возвращает строку из
     * символов '+', '^', '-' той же длины, что и слова.
     * Корректно обрабатывает повторяющиеся буквы двухпроходным алгоритмом:
     * сначала отмечаются все точные совпадения позиций, и только потом
     * оставшиеся буквы ищутся "не на своём месте", с учётом того, сколько
     * раз буква ещё встречается в загаданном слове.
     */
    public static String evaluate(String secret, String guess) {
        if (secret == null || guess == null || secret.length() != guess.length()) {
            throw new InternalGameStateException(
                    "Слова для сравнения должны быть одной длины и не равны null");
        }

        int length = secret.length();
        char[] result = new char[length];
        int[] letterCounts = new int[('я' - 'а') + 1];

        // Первый проход: точные совпадения по позиции.
        for (int i = 0; i < length; i++) {
            char secretChar = secret.charAt(i);
            char guessChar = guess.charAt(i);
            if (guessChar == secretChar) {
                result[i] = CORRECT_POSITION;
            } else {
                result[i] = 0; // временно не определено
                letterCounts[secretChar - 'а']++;
            }
        }

        // Второй проход: буква есть, но не на своём месте, либо отсутствует.
        StringBuilder feedback = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (result[i] == CORRECT_POSITION) {
                feedback.append(CORRECT_POSITION);
                continue;
            }
            char guessChar = guess.charAt(i);
            int idx = guessChar - 'а';
            if (idx >= 0 && idx < letterCounts.length && letterCounts[idx] > 0) {
                feedback.append(WRONG_POSITION);
                letterCounts[idx]--;
            } else {
                feedback.append(ABSENT);
            }
        }

        return feedback.toString();
    }

    public static boolean isWin(String feedback) {
        for (int i = 0; i < feedback.length(); i++) {
            if (feedback.charAt(i) != CORRECT_POSITION) {
                return false;
            }
        }
        return true;
    }
}
