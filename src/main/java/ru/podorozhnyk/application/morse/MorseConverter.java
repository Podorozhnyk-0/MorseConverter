package ru.podorozhnyk.application.morse;


import ru.podorozhnyk.application.exceptions.IllegalMorseSequenceException;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class MorseConverter {

    private final Set<MorseDictionary> DICTIONARIES;
    private Set<MorseDictionary> currentDictionaryOrder;

    public MorseConverter(MorseDictionary... dictionaries) {
        DICTIONARIES = new LinkedHashSet<>();
        DICTIONARIES.addAll(Arrays.stream(dictionaries).toList());

        currentDictionaryOrder = new LinkedHashSet<>();
        currentDictionaryOrder.addAll(DICTIONARIES);
    }

    public Set<MorseDictionary> getLoadedDictionaries() {
        return DICTIONARIES;
    }

    /**
     *
     * @param names list of string dictionary names. The number must match the number of dictionaries loaded to the converter.
     * @throws IllegalArgumentException if arguments count does not match number of dictionaries loaded to the converter
     * or if there's no registered dictionary with <code>name</code> amongst given <code>names</code>
     */
    public void setCurrentDictionaryOrder(String... names) {
        if (names.length != DICTIONARIES.size())
            throw new IllegalArgumentException(String.format("There's %d arguments, but there's only %d loaded dictionaries.", names.length, DICTIONARIES.size()));
        currentDictionaryOrder.clear();
        for (String id : names) {
            if (!containsDictionaryWithName(id))
                throw new IllegalArgumentException(String.format("There's no registered dictionary with name \"%s\"." +
                        " There's only %s dictionaries registered.",
                        id, String.join(", ", DICTIONARIES.stream().map(MorseDictionary::getName).toList())));
            currentDictionaryOrder.add(DICTIONARIES.stream().filter(x -> x.getName().equals(id)).findFirst().orElseThrow());
        }
    }

    public boolean containsDictionaryWithName(String name) {
        return DICTIONARIES.stream().anyMatch(x -> x.getName().equals(name));
    }

    public boolean containsTranslationCode(String translationCode) {
        return DICTIONARIES.stream().anyMatch(d -> d.containsTranslationCode(translationCode));
    }

    public boolean containsMorseCode(String morseCode) {
        return DICTIONARIES.stream().anyMatch(d -> d.containsMorseCode(morseCode));
    }

    /**
     * @return morse-converted text
     * @throws IllegalArgumentException if <code>text</code> is null or empty string.
     * @throws NoSuchElementException if <code>text</code> contains character that not registered in converter's dictionaries.
     */
    public String convertToMorse(String text) {
        Utils.requireNonBlank(text, "\"text\" is null or empty.");
        text = text.toUpperCase().replace('Ё', 'Е'); //трактуется одним кодом

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '\n') {
                builder.append('\n');
                continue;
            }
            if (text.charAt(i) == ' ') continue;

            boolean containsInDictionaries = false;
            for (MorseDictionary dictionary : currentDictionaryOrder) {
                if (!dictionary.containsTranslationCode(String.valueOf(text.charAt(i)))) continue;

                containsInDictionaries = true;
                String morseCode = dictionary.getMorseCode(String.valueOf(text.charAt(i)));
                builder.append(morseCode);
            }
            if (!containsInDictionaries)
                throw new NoSuchElementException(String.format("Couldn't find character \"%s\" in dictionaries.", text.charAt(i)));
            if (i < text.length() - 1) {
                if (text.charAt(i + 1) == ' ') {
                    builder.append("  ");
                }
                else {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }

    /**
     * @param morseText represents valid morse code
     * @return from morse-converted text
     * @throws IllegalArgumentException if <code>text</code> is null or empty string.
     * @throws IllegalMorseSequenceException if <code>text</code> has invalid morse sequences.
     */
    public String convertFromMorse(String morseText) throws IllegalMorseSequenceException {
        Utils.requireNonBlank(morseText, "\"morseText\" is null or empty.");
        if (!MorseUtils.isPatternValid(morseText))
            throw new IllegalArgumentException("\"morseText\" represents invalid morse code. Morse can contain only '.', '-' and spaces.");
        StringBuilder builder = new StringBuilder();

        for (String morseLine : morseText.split("\n")) {
            for (String word : morseLine.split(" {2}")) {
                String[] morseLetters = word.split(" ");
                for (String letter : morseLetters) {
                    if (!containsMorseCode(letter) && !letter.isBlank())
                        throw new IllegalMorseSequenceException(String.format("\"%s\" is non-existent morse sequence.", letter));
                    for (MorseDictionary dictionary : currentDictionaryOrder) {
                        if (!dictionary.containsMorseCode(letter)) continue;
                        builder.append(dictionary.getTranslationCode(letter));
                        break;
                    }
                }
                builder.append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
