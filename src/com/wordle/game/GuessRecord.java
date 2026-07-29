package com.wordle.game;

/**
 * Неизменяемая запись одной попытки игрока: само слово и результат
 * его сравнения с загаданным словом.
 */
public final class GuessRecord {

    private final String word;
    private final String feedback;

    public GuessRecord(String word, String feedback) {
        this.word = word;
        this.feedback = feedback;
    }

    public String getWord() {
        return word;
    }

    public String getFeedback() {
        return feedback;
    }
}
