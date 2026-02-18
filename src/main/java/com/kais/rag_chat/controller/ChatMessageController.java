package com.kais.rag_chat.controller;

import com.kais.rag_chat.dto.request.AddMessageRequest;
import com.kais.rag_chat.dto.response.ChatMessageResponse;
import com.kais.rag_chat.dto.response.PagedResponse;
import com.kais.rag_chat.mapper.ChatMessageMapper;
import com.kais.rag_chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedResponse<ChatMessageResponse>> getMessages(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageResponse> result = service.getMessagesBySession(sessionId, userId, page, size)
                .map(mapper::toResponse);
        return ResponseEntity.ok(PagedResponse.<ChatMessageResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }
}
