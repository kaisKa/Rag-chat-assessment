package com.kais.rag_chat.controller;

import com.kais.rag_chat.dto.request.AddMessageRequest;
import com.kais.rag_chat.dto.response.ChatMessageResponse;
import com.kais.rag_chat.mapper.ChatMessageMapper;
import com.kais.rag_chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions/{sessionId}/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService service;
    private final ChatMessageMapper mapper;

    @PostMapping
    public ResponseEntity<ChatMessageResponse> addMessage(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(service.addMessage(
                        sessionId, userId, request.getSender(), request.getContent(), request.getContext())));
    }

    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(mapper.toResponseList(service.getMessagesBySession(sessionId, userId)));
    }
}
