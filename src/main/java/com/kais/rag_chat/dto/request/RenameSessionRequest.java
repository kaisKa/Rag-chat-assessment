package com.kais.rag_chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RenameSessionRequest {

    @NotBlank(message = "New title is required")
    private String title;
}
