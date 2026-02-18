package com.kais.rag_chat.dto.request;

import com.kais.rag_chat.entity.Sender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class AddMessageRequest {

    @NotNull(message = "Sender is required")
    private Sender sender;

    @NotBlank(message = "Content is required")
    private String content;

    private String context;
}
