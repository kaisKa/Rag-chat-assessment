package com.kais.rag_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class Beans {

    @Bean
    public ObjectMapper createObjectMapper(){
        return new ObjectMapper();
    }
}
