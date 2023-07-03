package com.medicapp.server.config;


public class ExceptionHandlerConfig {
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }

    }
}
