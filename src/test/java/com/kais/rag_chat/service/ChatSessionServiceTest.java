package com.kais.rag_chat.service;

import com.kais.rag_chat.entity.ChatSession;
import com.kais.rag_chat.exception.SessionNotFoundException;
import com.kais.rag_chat.repository.ChatSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatSessionServiceTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @InjectMocks
    private ChatSessionService chatSessionService;

    private ChatSession buildSession(UUID id, String userId, String title, boolean isFavorite) {
        return ChatSession.builder()
                .id(id)
                .userId(userId)
                .title(title)
                .isFavorite(isFavorite)
                .build();
    }

    @Test
    void createSession_shouldSaveAndReturnSession() {
        String userId = "user-1";
        String title = "Test Session";
        ChatSession saved = buildSession(UUID.randomUUID(), userId, title, false);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(saved);

        ChatSession result = chatSessionService.createSession(userId, title);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTitle()).isEqualTo(title);
        verify(chatSessionRepository).save(any(ChatSession.class));
    }

    @Test
    void getSessionsByUser_shouldDelegateToRepository() {
        String userId = "user-1";
        List<ChatSession> sessions = List.of(buildSession(UUID.randomUUID(), userId, "S1", false));
        when(chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(sessions);

        List<ChatSession> result = chatSessionService.getSessionsByUser(userId);

        assertThat(result).hasSize(1);
        verify(chatSessionRepository).findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Test
    void getFavoriteSessionsByUser_shouldDelegateToRepository() {
        String userId = "user-1";
        List<ChatSession> favorites = List.of(buildSession(UUID.randomUUID(), userId, "Fav", true));
        when(chatSessionRepository.findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(userId)).thenReturn(favorites);

        List<ChatSession> result = chatSessionService.getFavoriteSessionsByUser(userId);

        assertThat(result).hasSize(1);
        verify(chatSessionRepository).findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(userId);
    }

    @Test
    void getSessionByIdAndUser_shouldReturnSession_whenFound() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = buildSession(sessionId, userId, "Test", false);
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(session));

        ChatSession result = chatSessionService.getSessionByIdAndUser(sessionId, userId);

        assertThat(result.getId()).isEqualTo(sessionId);
    }

    @Test
    void getSessionByIdAndUser_shouldThrow_whenNotFound() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chatSessionService.getSessionByIdAndUser(sessionId, userId))
                .isInstanceOf(SessionNotFoundException.class)
                .hasMessageContaining(sessionId.toString());
    }

    @Test
    void renameSession_shouldUpdateTitleAndSave() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = buildSession(sessionId, userId, "Old Title", false);
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(session));
        when(chatSessionRepository.save(session)).thenReturn(session);

        ChatSession result = chatSessionService.renameSession(sessionId, userId, "New Title");

        assertThat(result.getTitle()).isEqualTo("New Title");
        verify(chatSessionRepository).save(session);
    }

    @Test
    void toggleFavorite_shouldFlipFromFalseToTrue() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = buildSession(sessionId, userId, "Test", false);
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(session));
        when(chatSessionRepository.save(session)).thenReturn(session);

        ChatSession result = chatSessionService.toggleFavorite(sessionId, userId);

        assertThat(result.getIsFavorite()).isTrue();
    }

    @Test
    void toggleFavorite_shouldFlipFromTrueToFalse() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = buildSession(sessionId, userId, "Test", true);
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(session));
        when(chatSessionRepository.save(session)).thenReturn(session);

        ChatSession result = chatSessionService.toggleFavorite(sessionId, userId);

        assertThat(result.getIsFavorite()).isFalse();
    }

    @Test
    void deleteSession_shouldCallRepositoryDelete() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = buildSession(sessionId, userId, "Test", false);
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(session));

        chatSessionService.deleteSession(sessionId, userId);

        verify(chatSessionRepository).delete(session);
    }
}
