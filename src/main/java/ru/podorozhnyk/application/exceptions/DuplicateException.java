package ru.podorozhnyk.application.exceptions;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(Throwable throwable) { super(throwable); }
}
