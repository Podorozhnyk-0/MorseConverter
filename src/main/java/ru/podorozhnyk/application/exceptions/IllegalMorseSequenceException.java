package ru.podorozhnyk.application.exceptions;

public class IllegalMorseSequenceException extends Exception {
  public IllegalMorseSequenceException(String message) {
    super(message);
  }
  public IllegalMorseSequenceException(Throwable throwable) {
    super(throwable);
  }
}
