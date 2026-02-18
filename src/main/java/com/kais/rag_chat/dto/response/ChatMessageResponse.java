package com.kais.rag_chat.dto.response;

import com.kais.rag_chat.entity.Sender;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class ChatMessageResponse {

    private UUID id;
    private Sender sender;
    private String content;
    private String context;
    private LocalDateTime createdAt;
}
