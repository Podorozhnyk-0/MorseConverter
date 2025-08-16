package ru.podorozhnyk.application;

import ru.podorozhnyk.application.exceptions.ModeAlreadySetException;
import ru.podorozhnyk.application.morse.MorseDictionary;

import java.util.*;
import java.util.stream.Stream;

import static ru.podorozhnyk.application.morse.MorseUtils.DefaultDictionaries.*;

public class AppArguments {
    private boolean shouldPlayAudio;
    private boolean shouldUseFileAsSource;
    private AppMode mode = AppMode.NONE;
    private Set<MorseDictionary> order = new LinkedHashSet<>();

    private final List<String> input = new ArrayList<>();
    private boolean waitingForInput;

    public AppArguments(String[] args) throws ModeAlreadySetException {
        Objects.requireNonNull(args);
        parse(args);
    }

    private void parse(String[] args) throws ModeAlreadySetException {
        if (args.length == 0 || Arrays.asList(args).contains("--gui")) {
            setMode(AppMode.GUI);
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (isArgumentsSequence(arg)) {
                if (waitingForInput && input.isEmpty())
                    throw new IllegalArgumentException("There's no input data");
                waitingForInput = false;

                boolean isLongArg = arg.startsWith("--");
                arg = arg.replace("-", "");
                if (isLongArg) {
                    switch (arg) {
                        case "help" -> {
                            setMode(AppMode.HELP);
                            return;
                        }
                        case "order" -> waitingForInput = true;
                        default -> throw new IllegalArgumentException("Unknown long argument " + arg);
                    }
                }
                else {
                    for (char ch : arg.toCharArray()) {
                        switch (ch) {
                            case 't' -> {
                                setMode(AppMode.TEXT_TO_MORSE);
                                waitingForInput = true;
                            }
                            case 'm' -> {
                                setMode(AppMode.MORSE_TO_TEXT);
                                waitingForInput = true;
                            }
                            case 'i' -> shouldUseFileAsSource = true;
                            case 's' -> shouldPlayAudio = true;
                            default -> throw new IllegalArgumentException("Unknown short argument " + ch);
                        }
                    }
                }
            } else if (waitingForInput) {
                if (i > 0 && args[i - 1].equals("--order")) {
                    parseDictionaries(arg);
                    waitingForInput = false;
                } else {
                    input.add(arg);
                }

            } else {
                throw new IllegalArgumentException("Unknown argument " + arg);
            }

        }
        if (waitingForInput && input.isEmpty())
            throw new IllegalArgumentException("There's no input data");
    }

    private void parseDictionaries(String str) {
        for (String letter : str.split(",")) {
            MorseDictionary dictionary = MorseDictionary.getAllDictionaries()
                    .stream().filter(x -> x.getLetter().equalsIgnoreCase(letter))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(String.format("There's no registered dictionary with letter \"%s\"", letter)));
            order.add(dictionary);
        }
    }

    private boolean isArgumentsSequence(String str) {
        return str.matches("-{1,2}[^\\p{P}]+");
    }

    private void setMode(AppMode mode) throws ModeAlreadySetException {
        if (getMode() != AppMode.NONE)
            throw new ModeAlreadySetException(String.format("Mode already set to \"%s\"", getMode()), getMode(), mode);
        this.mode = mode;
    }

    public boolean shouldPlayAudio() {
        return shouldPlayAudio;
    }

    public boolean shouldUseFileAsSource() {
        return shouldUseFileAsSource;
    }

    public AppMode getMode() {
        return mode;
    }

    public List<String> getInput() {
        return input;
    }

    public Set<MorseDictionary> getOrder() {
        return order;
    }

    public enum AppMode {
        NONE,
        GUI,
        HELP,
        TEXT_TO_MORSE,
        MORSE_TO_TEXT;
    }
}
