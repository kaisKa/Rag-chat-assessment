package com.kais.rag_chat.config;

import com.kais.rag_chat.entity.ChatMessage;
import com.kais.rag_chat.entity.ChatSession;
import com.kais.rag_chat.entity.Sender;
import com.kais.rag_chat.repository.ChatMessageRepository;
import com.kais.rag_chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LogManager.getLogger(DataSeeder.class);
    private static final String SEED_USER = "demo-user";

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!sessionRepository.findByUserIdOrderByUpdatedAtDesc(SEED_USER).isEmpty()) {
            log.info("Seed data already exists, skipping.");
            return;
        }

        log.info("Seeding initial data for user '{}'...", SEED_USER);

        // Session 1: General RAG introduction
        ChatSession session1 = sessionRepository.save(ChatSession.builder()
                .userId(SEED_USER)
                .title("What is RAG?")
                .isFavorite(true)
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session1)
                .sender(Sender.USER)
                .content("Can you explain what RAG is?")
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session1)
                .sender(Sender.ASSISTANT)
                .content("RAG stands for Retrieval-Augmented Generation. It enhances language models by retrieving relevant context from a knowledge base before generating a response, making answers more accurate and grounded in your own data.")
                .context("Source: RAG whitepaper, 2020 — Lewis et al.")
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session1)
                .sender(Sender.USER)
                .content("What are the main components of a RAG system?")
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session1)
                .sender(Sender.ASSISTANT)
                .content("A RAG system has three main components: a retriever that searches a vector store for relevant documents, a generator (LLM) that produces the response, and a knowledge base where your documents are stored as embeddings.")
                .context("Source: RAG architecture overview, internal docs.")
                .build());

        // Session 2: Spring Boot question
        ChatSession session2 = sessionRepository.save(ChatSession.builder()
                .userId(SEED_USER)
                .title("Spring Boot Best Practices")
                .isFavorite(false)
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session2)
                .sender(Sender.USER)
                .content("What are some best practices for building microservices with Spring Boot?")
                .build());

        messageRepository.save(ChatMessage.builder()
                .session(session2)
                .sender(Sender.ASSISTANT)
                .content("Key best practices include: using environment variables for configuration, implementing health checks with Actuator, centralizing error handling with @RestControllerAdvice, adding rate limiting, securing endpoints, and containerizing with Docker.")
                .context("Source: Spring Boot documentation, microservices patterns guide.")
                .build());

        // Session 3: Empty session
        sessionRepository.save(ChatSession.builder()
                .userId(SEED_USER)
                .title("New Conversation")
                .isFavorite(false)
                .build());

        log.info("Seed data created successfully: 3 sessions, 6 messages for user '{}'.", SEED_USER);
    }
}
