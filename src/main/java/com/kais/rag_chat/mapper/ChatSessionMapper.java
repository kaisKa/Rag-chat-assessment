package com.kais.rag_chat.mapper;

import com.kais.rag_chat.dto.response.ChatSessionResponse;
import com.kais.rag_chat.entity.ChatSession;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    ChatSessionResponse toResponse(ChatSession session);

    List<ChatSessionResponse> toResponseList(List<ChatSession> sessions);
}
