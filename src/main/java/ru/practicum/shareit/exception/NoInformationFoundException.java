package ru.practicum.shareit.exception;

public class NoInformationFoundException extends RuntimeException {
    public NoInformationFoundException(String message) {
        super(message);
    }
}