package com.kais.rag_chat.repository;

import com.kais.rag_chat.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);

    List<ChatSession> findByUserIdAndIsFavoriteTrueOrderByUpdatedAtDesc(String userId);

    Optional<ChatSession> findByIdAndUserId(UUID id, String userId);
}
