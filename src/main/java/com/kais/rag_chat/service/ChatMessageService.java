package com.kais.rag_chat.service;

import com.kais.rag_chat.entity.ChatMessage;
import com.kais.rag_chat.entity.ChatSession;
import com.kais.rag_chat.entity.Sender;
import com.kais.rag_chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionService chatSessionService;

    public ChatMessage addMessage(UUID sessionId, String userId, Sender sender, String content, String context) {
        ChatSession session = chatSessionService.getSessionByIdAndUser(sessionId, userId);
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .sender(sender)
                .content(content)
                .context(context)
                .build();
        return chatMessageRepository.save(message);
    }

    public Page<ChatMessage> getMessagesBySession(UUID sessionId, String userId, int page, int size) {
        chatSessionService.getSessionByIdAndUser(sessionId, userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
    }
}
