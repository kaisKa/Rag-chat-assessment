package com.kais.rag_chat.service;

import com.kais.rag_chat.entity.ChatMessage;
import com.kais.rag_chat.entity.ChatSession;
import com.kais.rag_chat.entity.Sender;
import com.kais.rag_chat.exception.SessionNotFoundException;
import com.kais.rag_chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatSessionService chatSessionService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    void addMessage_shouldSaveMessageWithCorrectFields() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = ChatSession.builder().id(sessionId).userId(userId).title("Test").build();
        when(chatSessionService.getSessionByIdAndUser(sessionId, userId)).thenReturn(session);

        ChatMessage saved = ChatMessage.builder()
                .id(UUID.randomUUID())
                .session(session)
                .sender(Sender.USER)
                .content("hello")
                .context("ctx")
                .build();
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessage result = chatMessageService.addMessage(sessionId, userId, Sender.USER, "hello", "ctx");

        assertThat(result.getSender()).isEqualTo(Sender.USER);
        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getContext()).isEqualTo("ctx");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void addMessage_shouldThrow_whenSessionNotFound() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        when(chatSessionService.getSessionByIdAndUser(sessionId, userId))
                .thenThrow(new SessionNotFoundException(sessionId));

        assertThatThrownBy(() -> chatMessageService.addMessage(sessionId, userId, Sender.USER, "hello", null))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    void getMessagesBySession_shouldReturnPagedMessages() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        ChatSession session = ChatSession.builder().id(sessionId).userId(userId).title("Test").build();
        when(chatSessionService.getSessionByIdAndUser(sessionId, userId)).thenReturn(session);

        Page<ChatMessage> page = new PageImpl<>(List.of(
                ChatMessage.builder().id(UUID.randomUUID()).sender(Sender.USER).content("hi").build()
        ));
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(eq(sessionId), any(Pageable.class)))
                .thenReturn(page);

        Page<ChatMessage> result = chatMessageService.getMessagesBySession(sessionId, userId, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        verify(chatMessageRepository).findBySessionIdOrderByCreatedAtAsc(eq(sessionId), any(Pageable.class));
    }

    @Test
    void getMessagesBySession_shouldThrow_whenSessionNotFound() {
        UUID sessionId = UUID.randomUUID();
        String userId = "user-1";
        when(chatSessionService.getSessionByIdAndUser(sessionId, userId))
                .thenThrow(new SessionNotFoundException(sessionId));

        assertThatThrownBy(() -> chatMessageService.getMessagesBySession(sessionId, userId, 0, 20))
                .isInstanceOf(SessionNotFoundException.class);
    }
}
