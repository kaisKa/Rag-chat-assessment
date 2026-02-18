package com.kais.rag_chat.controller;

import com.kais.rag_chat.dto.request.CreateSessionRequest;
import com.kais.rag_chat.dto.request.RenameSessionRequest;
import com.kais.rag_chat.dto.response.ChatSessionResponse;
import com.kais.rag_chat.mapper.ChatSessionMapper;
import com.kais.rag_chat.service.ChatSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService service;
    private final ChatSessionMapper mapper;

    @PostMapping
    public ResponseEntity<ChatSessionResponse> create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(service.createSession(userId, request.getTitle())));
    }

    @GetMapping
    public ResponseEntity<List<ChatSessionResponse>> getAll(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(mapper.toResponseList(service.getSessionsByUser(userId)));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ChatSessionResponse>> getFavorites(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(mapper.toResponseList(service.getFavoriteSessionsByUser(userId)));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ChatSessionResponse> getById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(mapper.toResponse(service.getSessionByIdAndUser(sessionId, userId)));
    }

    @PatchMapping("/{sessionId}/rename")
    public ResponseEntity<ChatSessionResponse> rename(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody RenameSessionRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.renameSession(sessionId, userId, request.getTitle())));
    }

    @PatchMapping("/{sessionId}/favorite")
    public ResponseEntity<ChatSessionResponse> toggleFavorite(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(mapper.toResponse(service.toggleFavorite(sessionId, userId)));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId) {
        service.deleteSession(sessionId, userId);
        return ResponseEntity.noContent().build();
    }
}
