package ru.podorozhnyk.application;

import ru.podorozhnyk.application.exceptions.IllegalMorseSequenceException;
import ru.podorozhnyk.application.exceptions.ModeAlreadySetException;
import ru.podorozhnyk.application.morse.MorseAudioGenerator;
import ru.podorozhnyk.application.morse.MorseConverter;
import ru.podorozhnyk.application.morse.MorseDictionary;
import ru.podorozhnyk.application.ui.MainFrame;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static ru.podorozhnyk.application.morse.MorseUtils.DefaultDictionaries.*;

public class Main {
    public static void main(String[] args) {
        MorseDictionary[] dictionaries = new MorseDictionary[] { LATIN, CYRILLIC, NUMBERS, PUNCTUATION };
        try {
            AppArguments appArguments = new AppArguments(args);

            if (appArguments.getMode() == AppArguments.AppMode.GUI) {
                launchUi();
            } else {
                if (!appArguments.getOrder().isEmpty()) {
                    dictionaries = appArguments.getOrder().toArray(MorseDictionary[]::new);
                }
                final MorseConverter CONVERTER = new MorseConverter(dictionaries);
                StringBuilder input = new StringBuilder();
                String morseCode = null;
                if (appArguments.shouldUseFileAsSource()) {
                    for (String line : appArguments.getInput()) {
                        File file = new File(line);
                        if (!file.exists() || !file.canRead())
                            throw new FileNotFoundException(String.format("Couldn't find file \"%s\"", line));
                        input.append(String.join("\n", Files.readAllLines(file.toPath()))).append("\n");
                    }
                }
                else {
                    input.append(String.join("\n", appArguments.getInput()));
                }

                switch (appArguments.getMode()) {
                    case TEXT_TO_MORSE -> {
                        morseCode = CONVERTER.convertToMorse(input.toString());
                        System.out.println(morseCode);
                    }
                    case MORSE_TO_TEXT -> {
                        morseCode = input.toString();
                        System.out.println(CONVERTER.convertFromMorse(morseCode));
                    }
                    case HELP -> showHelp();
                    default -> throw new IllegalStateException("Translation mode wasn't identified.");
                }
                if (appArguments.shouldPlayAudio()) {
                    MorseAudioGenerator.generateMorseCode(morseCode);
                    MorseAudioGenerator.playMorse(true);
                }

            }

        } catch (ModeAlreadySetException e) {
            catchExceptions("Mutually exclusive operations.");
        } catch (IllegalArgumentException | IllegalStateException | IOException | IllegalMorseSequenceException |
                 NoSuchElementException e) {
            catchExceptions(e.getMessage());
        }
    }

    private static void launchUi() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        new MainFrame("Morse Converter", screenSize.width / 2, screenSize.height / 2);
    }

    private static void catchExceptions(String message) {
        System.err.println("\033[0;31mERROR: " + message + "\033[0m");
        System.err.println("Run application with \"--help\" flag to get usage info.");
    }

    private static void showHelp() {
        System.out.println("List of application flags: ");
        int i = 0;
        System.out.printf("%d) --help\tProvides application usage info.\n", ++i);
        System.out.printf("%d) --gui\tRuns GUI version of application.\n", ++i);
        System.out.printf("%d) -t\t\tText-To-Morse mode. Requires text source.\n", ++i);
        System.out.printf("%d) -m\t\tMorse-To-Text mode. Requires valid morse-code source.\n", ++i);
        System.out.printf("%d) -s\t\tSound mode. Plays morse-code after conversion.\n", ++i);
        System.out.printf("%d) -i\t\tInput mode. Makes file source of text instead of stdin.\n", ++i);
        System.out.printf("""
                %d) --order\tAllows to set dictionaries priority. Dictionaries letters must be separated by comma.\
                
                \t\tThere are 4 dictionaries: Latin (l/L), Cyrillic (c/C), Numbers (n/N) and Punctuation (p,P).\
                
                \t\tE.g.: "--order c,l,n,p" will make morse-to-text mode translate morse to cyrillic firstly. Default order is [L,C,N,P].
                """, ++i);

    }
}
