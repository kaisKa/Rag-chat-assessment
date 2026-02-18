package com.kais.rag_chat.service;

import com.kais.rag_chat.entity.ChatSession;
import com.kais.rag_chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSession createSession(String userId, String title) {
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .title(title)
                .build();
        return chatSessionRepository.save(session);
    }

    public List<ChatSession> getSessionsByUser(String userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public List<ChatSession> getFavoriteSessionsByUser(String userId) {
        return chatSessionRepository.findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(userId);
    }

    public ChatSession getSessionByIdAndUser(UUID sessionId, String userId) {
        return chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    public ChatSession renameSession(UUID sessionId, String userId, String newTitle) {
        ChatSession session = getSessionByIdAndUser(sessionId, userId);
        session.setTitle(newTitle);
        return chatSessionRepository.save(session);
    }

    public ChatSession toggleFavorite(UUID sessionId, String userId) {
        ChatSession session = getSessionByIdAndUser(sessionId, userId);
        session.setIsFavorite(!session.getIsFavorite());
        return chatSessionRepository.save(session);
    }

    public void deleteSession(UUID sessionId, String userId) {
        ChatSession session = getSessionByIdAndUser(sessionId, userId);
        chatSessionRepository.delete(session);
    }
}
