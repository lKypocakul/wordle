package com.wordle.game;

/**
 * Простой автономный тест для WordEvaluator, не требующий JUnit.
 * WordEvaluator не обращается к консоли и не зависит от Main, поэтому
 * его удобно тестировать в изоляции — это и демонстрирует данный класс.
 * Запуск: java -cp out com.wordle.game.WordEvaluatorTest
 */
public final class WordEvaluatorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        check("полное совпадение", "слово", "слово", "+++++");
        check("полное несовпадение", "топор", "клинч", "-----");
        check("буквы не на своих местах", "весна", "навес", "^^^^^");
        check("повторяющиеся буквы: ровно совпадает по счётчику",
                "топор", "робот", "^+-+^");
        check("повторяющиеся буквы: лишние вхождения помечаются как отсутствие",
                "топор", "особо", "^-^--");
        check("isWin true для выигрышной подсказки", null, null, null); // см. checkIsWin ниже

        boolean win1 = WordEvaluator.isWin("+++++");
        boolean win2 = WordEvaluator.isWin("++++-");
        report("isWin(\"+++++\") == true", win1);
        report("isWin(\"++++-\") == false", !win2);

        System.out.println();
        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String description, String secret, String guess, String expected) {
        if (secret == null) {
            return; // используется только как заголовок-заглушка для группы isWin-проверок выше
        }
        String actual = WordEvaluator.evaluate(secret, guess);
        boolean ok = actual.equals(expected);
        report(description + " (secret=" + secret + ", guess=" + guess
                + ", expected=" + expected + ", actual=" + actual + ")", ok);
    }

    private static void report(String description, boolean ok) {
        System.out.println((ok ? "PASS: " : "FAIL: ") + description);
        if (ok) {
            passed++;
        } else {
            failed++;
        }
    }
}
