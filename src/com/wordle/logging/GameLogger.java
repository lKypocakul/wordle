package com.wordle.logging;

import com.wordle.exceptions.LogInitializationException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Тонкая обёртка над {@link PrintWriter}, которая передаётся во все
 * классы программы вместо прямой работы с консолью. Ни один класс,
 * кроме главного класса игры, не должен писать в System.out — вместо
 * этого он пишет в лог через этот класс, что упрощает юнит-тестирование.
 */
public final class GameLogger implements AutoCloseable {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PrintWriter writer;

    public GameLogger(String logFilePath) throws LogInitializationException {
        try {
            // autoFlush = true, чтобы записи попадали в файл сразу же,
            // это важно, если программа завершится аварийно.
            this.writer = new PrintWriter(new FileWriter(logFilePath, true), true);
        } catch (IOException e) {
            throw new LogInitializationException(
                    "Не удалось создать или открыть лог-файл: " + logFilePath, e);
        }
    }

    public void info(String message) {
        write("INFO", message);
    }

    public void state(String message) {
        write("STATE", message);
    }

    public void error(String message, Throwable cause) {
        write("ERROR", message + (cause != null ? " | " + describe(cause) : ""));
    }

    private void write(String level, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        writer.println("[" + timestamp + "] [" + level + "] " + message);
    }

    private String describe(Throwable cause) {
        StringBuilder sb = new StringBuilder();
        sb.append(cause.getClass().getSimpleName());
        if (cause.getMessage() != null) {
            sb.append(": ").append(cause.getMessage());
        }
        return sb.toString();
    }

    @Override
    public void close() {
        writer.close();
    }
}
