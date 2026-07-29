package com.wordle.util;

import java.util.Locale;

/**
 * Утилитный класс для приведения слов к единому виду, который
 * используется во всей игре: и для слов из словаря, и для ввода игрока.
 */
public final class WordNormalizer {

    private static final Locale RU = new Locale("ru");

    private WordNormalizer() {
        // утилитный класс, экземпляры не создаются
    }

    /**
     * Приводит слово к нормальной для игры форме:
     * убирает пробелы по краям, переводит в нижний регистр
     * и заменяет букву "ё" на "е", так как в игре они равнозначны.
     */
    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String result = raw.trim().toLowerCase(RU);
        result = result.replace('ё', 'е');
        return result;
    }

    /**
     * Проверяет, что строка состоит только из букв русского алфавита
     * (после нормализации, то есть буква "ё" уже считается заменённой).
     */
    public static boolean isCyrillicOnly(String normalized) {
        if (normalized.isEmpty()) {
            return false;
        }
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c < 'а' || c > 'я') {
                return false;
            }
        }
        return true;
    }
}
