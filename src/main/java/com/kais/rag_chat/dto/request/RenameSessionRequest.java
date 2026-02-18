package com.kais.rag_chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class RenameSessionRequest {

    @NotBlank(message = "New title is required")
    private String title;
}
