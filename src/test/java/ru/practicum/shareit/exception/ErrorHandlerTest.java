package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidException() {
        ValidException exception = new ValidException("Test Valid Exception");

        ErrorResponse response = errorHandler.handleValidException(exception);

        assertEquals("Test Valid Exception", response.getError());
    }

    @Test
    void handleObjectNotFoundException() {
        ObjectNotFoundException exception = new ObjectNotFoundException("Test Object Not Found Exception");

        ErrorResponse response = errorHandler.handleObjectNotFoundException(exception);

        assertEquals("Test Object Not Found Exception", response.getError());
    }

    @Test
    void handleExistException() {
        ExistException exception = new ExistException("Test Exist Exception");

        ErrorResponse response = errorHandler.handleExistException(exception);

        assertEquals("Test Exist Exception", response.getError());
    }

    @Test
    void handleThrowable() {
        Throwable throwable = new Throwable("Test Throwable");

        ErrorResponse response = errorHandler.handleThrowable(throwable);

        assertEquals("Test Throwable", response.getError());
    }
}
