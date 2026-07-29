# Wordle на Java

## Структура проекта

```
src/com/wordle/
  Main.java                     — главный класс, единственный работает с консолью
  dictionary/GameDictionary.java — загрузка и фильтрация словаря
  game/
    WordEvaluator.java          — алгоритм сравнения слов (+, ^, -)
    GameState.java              — состояние партии, попытки, ограничения для подсказок
    GuessRecord.java            — запись одной попытки
    HintEngine.java             — поиск слов-кандидатов для подсказки
    InputValidator.java         — проверка корректности ввода игрока
  logging/GameLogger.java       — обёртка над PrintWriter, лог-файл
  exceptions/                   — иерархия исключений (программные и игровые)
  util/WordNormalizer.java      — нормализация слов (нижний регистр, ё→е)
resources/dictionary.txt        — словарь существительных (не отсортирован по длине)
tests/com/wordle/game/WordEvaluatorTest.java — автономный тест алгоритма сравнения
```

## Сборка и запуск

Требуется JDK 11+. Запускать команды из корня проекта (`wordle/`), чтобы
относительные пути `resources/dictionary.txt` и `wordle.log` совпадали
с ожиданиями `Main.java`.

```bash
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -Dstdout.encoding=UTF-8 -cp out com.wordle.Main
```

Если консоль показывает вопросики вместо кириллицы — терминал не в UTF-8,
добавьте `-Dstdout.encoding=UTF-8` (как выше) или настройте кодировку терминала.

## Как играть

* Введите слово из 5 русских букв и нажмите Enter — получите подсказку из
  символов `+` (буква на своём месте), `^` (буква есть, но не там) и `-`
  (буквы нет в слове).
* Просто нажмите Enter на пустой строке — компьютер сам подберёт и
  "введёт" подходящее слово с учётом уже полученных подсказок. Если нажимать
  Enter каждый раз, компьютер пройдёт всю игру самостоятельно.
* На игру даётся 6 попыток. Некорректный ввод (не та длина, не кириллица,
  слова нет в словаре) не тратит попытку.
* Все события игры и ошибки пишутся в файл `wordle.log` рядом с проектом.

## Тесты

`WordEvaluator` не зависит ни от консоли, ни от других классов игры, поэтому
тестируется в изоляции:

```bash
javac -encoding UTF-8 -d out @sources.txt tests/com/wordle/game/WordEvaluatorTest.java
java -Dstdout.encoding=UTF-8 -cp out com.wordle.game.WordEvaluatorTest
```
