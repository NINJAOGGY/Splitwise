package com.aditya.splitwise.exception;

public class GroupNotFoundException
        extends RuntimeException {

    public GroupNotFoundException(
            String message) {
        super(message);
    }
}