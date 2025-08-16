package ru.podorozhnyk.application.morse;

import java.util.HashSet;
import java.util.Set;

public final class MorseUtils {
    private static final Set<String> MORSE_CODES;

    private MorseUtils() {}

    static {
        MORSE_CODES = new HashSet<>();
        MORSE_CODES.add(".-");
        MORSE_CODES.add("-...");
        MORSE_CODES.add("-.-.");
        MORSE_CODES.add("-..");
        MORSE_CODES.add(".");
        MORSE_CODES.add("..-.");
        MORSE_CODES.add("--.");
        MORSE_CODES.add("....");
        MORSE_CODES.add("..");
        MORSE_CODES.add(".---");
        MORSE_CODES.add("-.-");
        MORSE_CODES.add(".-..");
        MORSE_CODES.add("--");
        MORSE_CODES.add("-.");
        MORSE_CODES.add("---");
        MORSE_CODES.add(".--.");
        MORSE_CODES.add("--.-");
        MORSE_CODES.add(".-.");
        MORSE_CODES.add("...");
        MORSE_CODES.add("-");
        MORSE_CODES.add("..-");
        MORSE_CODES.add("...-");
        MORSE_CODES.add(".--");
        MORSE_CODES.add("-..-");
        MORSE_CODES.add("-.--");
        MORSE_CODES.add("--..");
        MORSE_CODES.add("---.");
        MORSE_CODES.add("----");
        MORSE_CODES.add("--.--");
        MORSE_CODES.add("..-..");
        MORSE_CODES.add("..--");
        MORSE_CODES.add(".-.-");
        MORSE_CODES.add(".----");
        MORSE_CODES.add("..---");
        MORSE_CODES.add("...--");
        MORSE_CODES.add("....-");
        MORSE_CODES.add(".....");
        MORSE_CODES.add("-....");
        MORSE_CODES.add("--...");
        MORSE_CODES.add("---..");
        MORSE_CODES.add("----.");
        MORSE_CODES.add("-----");
        MORSE_CODES.add(".-.-.-");
        MORSE_CODES.add("--..--");
        MORSE_CODES.add("..--..");
        MORSE_CODES.add(".----.");
        MORSE_CODES.add("-.-.--");
        MORSE_CODES.add("-..-.");
        MORSE_CODES.add("-.--.");
        MORSE_CODES.add("-.--.-");
        MORSE_CODES.add(".-...");
        MORSE_CODES.add("---...");
        MORSE_CODES.add("-.-.-.");
        MORSE_CODES.add("-...-");
        MORSE_CODES.add(".-.-.");
        MORSE_CODES.add("-....-");
        MORSE_CODES.add("..--.-");
        MORSE_CODES.add(".-..-.");
        MORSE_CODES.add("...-..-");
        MORSE_CODES.add(".--.-.");

    }

    public static boolean morseCodeExists(String morseCode) {
        return MORSE_CODES.contains(morseCode);
    }

    public static boolean isPatternValid(String morseText) {
        return morseText.matches("[-. \n]+");
    }

    public static class DefaultDictionaries {
        public static final MorseDictionary LATIN = new MorseDictionary.Builder("LATIN", "L")
                .addMapping(".-", "A")
                .addMapping("-...", "B")
                .addMapping("-.-.", "C")
                .addMapping("-..", "D")
                .addMapping(".", "E")
                .addMapping("..-.", "F")
                .addMapping("--.", "G")
                .addMapping("....", "H")
                .addMapping("..", "I")
                .addMapping(".---", "J")
                .addMapping("-.-", "K")
                .addMapping(".-..", "L")
                .addMapping("--", "M")
                .addMapping("-.", "N")
                .addMapping("---", "O")
                .addMapping(".--.", "P")
                .addMapping("--.-", "Q")
                .addMapping(".-.", "R")
                .addMapping("...", "S")
                .addMapping("-", "T")
                .addMapping("..-", "U")
                .addMapping("...-", "V")
                .addMapping(".--", "W")
                .addMapping("-..-", "X")
                .addMapping("-.--", "Y")
                .addMapping("--..", "Z")
                .addMapping("---.", "Ö")
                .addMapping("----", "Š")
                .addMapping("--.--", "Ñ")
                .addMapping("..-..", "É")
                .addMapping("..--", "Ü")
                .addMapping(".-.-", "Ä").build();
        public static final MorseDictionary CYRILLIC = new MorseDictionary.Builder("CYRILLIC", "C")
                .addMapping(".-", "А")
                .addMapping("-...", "Б")
                .addMapping(".--", "В")
                .addMapping("--.", "Г")
                .addMapping("-..", "Д")
                .addMapping(".", "Е")
                .addMapping("...-", "Ж")
                .addMapping("--..", "З")
                .addMapping("..", "И")
                .addMapping(".---", "Й")
                .addMapping("-.-", "К")
                .addMapping(".-..", "Л")
                .addMapping("--", "М")
                .addMapping("-.", "Н")
                .addMapping("---", "О")
                .addMapping(".--.", "П")
                .addMapping(".-.", "Р")
                .addMapping("...", "С")
                .addMapping("-", "Т")
                .addMapping("..-", "У")
                .addMapping("..-.", "Ф")
                .addMapping("....", "Х")
                .addMapping("-.-.", "Ц")
                .addMapping("---.", "Ч")
                .addMapping("----", "Ш")
                .addMapping("--.-", "Щ")
                .addMapping("--.--", "Ъ")
                .addMapping("-.--", "Ы")
                .addMapping("-..-", "Ь")
                .addMapping("..-..", "Э")
                .addMapping("..--", "Ю")
                .addMapping(".-.-", "Я").build();
        public static final MorseDictionary NUMBERS = new MorseDictionary.Builder("NUMBERS", "N")
                .addMapping("-----", "0")
                .addMapping(".----", "1")
                .addMapping("..---", "2")
                .addMapping("...--", "3")
                .addMapping("....-", "4")
                .addMapping(".....", "5")
                .addMapping("-....", "6")
                .addMapping("--...", "7")
                .addMapping("---..", "8")
                .addMapping("----.", "9").build();
        public static final MorseDictionary PUNCTUATION = new MorseDictionary.Builder("PUNCTUATION", "P")
                .addMapping(".-.-.-", ".")
                .addMapping("--..--", ",")
                .addMapping("---...", ":")
                .addMapping("-.-.-.", ";")
                .addMapping("..--..", "?")
                .addMapping("-.-.--", "!")
                .addMapping("-....-", "-")
                .addMapping("-..-.", "/")
                .addMapping("-.--.", "(")
                .addMapping("-.--.-", ")")
                .addMapping(".-..-.", "\"")
                .addMapping(".----.", "'")
                .addMapping("-...-", "=")
                .addMapping(".-.-.", "+")
                .addMapping(".--.-.", "@")
                .addMapping("...-..-", "$")
                .addMapping("..--.-", "_")
                .addMapping(".-...", "&").build();
    }
}
