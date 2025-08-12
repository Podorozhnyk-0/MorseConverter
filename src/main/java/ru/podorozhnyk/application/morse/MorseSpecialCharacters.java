package ru.podorozhnyk.application.morse;

import java.util.HashSet;
import java.util.Set;

@Deprecated(forRemoval = true)
public enum MorseSpecialCharacters {

    BRACKETS;


    private final String characterId;

    MorseSpecialCharacters() {
        characterId = this.getDeclaringClass().getTypeName();
        MorseSpecialSet.morseSpecial.add(toString().toUpperCase());
    }

    @Override
    public String toString() {
        return characterId + "#" + this.name();
    }

    public static boolean isSpecialCharacter(String character) {
        return MorseSpecialSet.morseSpecial.contains(character.toUpperCase());
    }

    private static class MorseSpecialSet {
        private final static Set<String> morseSpecial = new HashSet<>();

    }
}
