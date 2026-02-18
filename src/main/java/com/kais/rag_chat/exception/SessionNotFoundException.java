package com.kais.rag_chat.exception;

import java.util.UUID;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(UUID sessionId) {
        super("Session not found with id: " + sessionId);
    }
}