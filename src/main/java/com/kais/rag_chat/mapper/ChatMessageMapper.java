package com.kais.rag_chat.mapper;

import com.kais.rag_chat.dto.response.ChatMessageResponse;
import com.kais.rag_chat.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    ChatMessageResponse toResponse(ChatMessage message);

    List<ChatMessageResponse> toResponseList(List<ChatMessage> messages);
}
