package com.example.housekeeptrack.exception;

public class InvalidRoomStatusTransitionException extends RuntimeException {
    public InvalidRoomStatusTransitionException(String message) {
        super(message);
    }
}
