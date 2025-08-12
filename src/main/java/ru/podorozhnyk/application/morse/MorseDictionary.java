package ru.podorozhnyk.application.morse;

import ru.podorozhnyk.application.exceptions.DuplicateException;

import java.util.*;

public class MorseDictionary {
    private final String DICTIONARY_NAME;
    private final Map<String, String> MORSE_TO_TEXT, TEXT_TO_MORSE;

    private MorseDictionary(final String name, final Map<String, String> morseToText, final Map<String, String> textToMorse) {
        DICTIONARY_NAME = name;
        MORSE_TO_TEXT = morseToText;
        TEXT_TO_MORSE = textToMorse;
    }

    public boolean containsMorseCode(String morseCode) {
        return MORSE_TO_TEXT.containsKey(morseCode);
    }

    public boolean containsTranslationCode(String translationCode) {
        return TEXT_TO_MORSE.containsKey(translationCode);
    }

    public String getName() {
        return DICTIONARY_NAME;
    }

    /**
     *
     * @param translationCode registered translation code for this dictionary
     * @return valid morse code for corresponding translation code
     * @throws NoSuchElementException if there's no morse code that associates with translation code
     */
    public String getMorseCode(String translationCode) throws NoSuchElementException {
        if (!containsTranslationCode(translationCode))
            throw new NoSuchElementException(
                    String.format("There's no such element that translates with \"%s\"", translationCode));
        return TEXT_TO_MORSE.get(translationCode);
    }

    /**
     *
     * @param morseCode valid and registered morse code
     * @return translation code for corresponding morse code
     * @throws NoSuchElementException if there's no such morse code registered
     */
    public String getTranslationCode(String morseCode) throws NoSuchElementException {
        if (!containsMorseCode(morseCode))
            throw new NoSuchElementException(String.format("There's no registered morse code \"%s\".", morseCode));
        return MORSE_TO_TEXT.get(morseCode);
    }


    private static class IdsHolder {
        private final static Set<String> IDS = Collections.synchronizedSet(new HashSet<>());
    }

    public static class Builder {
        private final String DICTIONARY_NAME;
        private final Map<String, String> MORSE_TO_TEXT, TEXT_TO_MORSE;

        /**
         *
         * @param name unique non-null not empty ID for dictionary
         * @throws DuplicateException if there's already registered dictionary with same <code>name</code>
         * @throws IllegalArgumentException if <code>name</code> is null or empty
         */
        public Builder(String name) {
            Utils.requireNonBlank(name, "\"name\" is null or empty.");
            if (IdsHolder.IDS.contains(name))
                throw new DuplicateException(String.format("There's already registered dictionary with name \"%s\".", name));
            DICTIONARY_NAME = name;
            MORSE_TO_TEXT = new HashMap<>();
            TEXT_TO_MORSE = new HashMap<>();
            IdsHolder.IDS.add(DICTIONARY_NAME);
        }

        /**
         *
         * @param morseCharacter non-null valid morse character string
         * @param translationCharacter non-null dictionary translation
         * @throws IllegalArgumentException if <code>morseCharacter</code> is non-valid character or null or empty
         * or <code>translationCharacter</code> null or empty
         * @throws DuplicateException if this dictionary already contains that morse and/or translation codes
         */
        public Builder addMapping(String morseCharacter, String translationCharacter) {
            Utils.requireNonBlank(morseCharacter, "\"morseCharacter\" is null or empty string.");
            Utils.requireNonBlank(translationCharacter, "\"translationCharacter\" is null or empty string.");

            if (!MorseUtils.isPatternValid(morseCharacter))
                throw new IllegalArgumentException("\"morseCharacter\" represents invalid morse code. Morse can contain only '.', '-' and spaces.");
            if (!MorseUtils.morseCodeExists(morseCharacter))
                throw new IllegalArgumentException(String.format("Non-existent morse code \"%s\"", morseCharacter));
            translationCharacter = translationCharacter.toUpperCase();

            if (TEXT_TO_MORSE.containsKey(translationCharacter))
                throw new DuplicateException(String.format("This dictionary already has mapping for translation code \"%s\"", translationCharacter));
            if (MORSE_TO_TEXT.containsKey(morseCharacter))
                throw new DuplicateException(String.format("This dictionary already has mapping for morse code \"%s\"", morseCharacter));

            MORSE_TO_TEXT.put(morseCharacter, translationCharacter);
            TEXT_TO_MORSE.put(translationCharacter, morseCharacter);
            return this;
        }

        public MorseDictionary build() {
            return new MorseDictionary(DICTIONARY_NAME, MORSE_TO_TEXT, TEXT_TO_MORSE);
        }
    }
}
