package com.kaushik.travelplan.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String id) {
        super(resource + " not found with ID: " + id);
    }
}
