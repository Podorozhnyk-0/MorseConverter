package ru.podorozhnyk.application.exceptions;

import ru.podorozhnyk.application.AppArguments;

public class ModeAlreadySetException extends Exception {
    private final AppArguments.AppMode currentMode, newMode;
    public ModeAlreadySetException(String message, AppArguments.AppMode currentMode, AppArguments.AppMode newMode) {
        super(message);
        this.currentMode = currentMode;
        this.newMode = newMode;
    }

    public AppArguments.AppMode getCurrentMode() { return currentMode; }
    public AppArguments.AppMode getNewMode() { return newMode; }
}
