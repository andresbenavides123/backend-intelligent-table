package com.edulive.infrastructure.config;

import com.edulive.application.service.ExerciseService;
import com.edulive.domain.port.out.AIAnalyzerPort;
import com.edulive.domain.port.out.ExerciseRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ExerciseService exerciseService(AIAnalyzerPort aiAnalyzerPort, ExerciseRepositoryPort exerciseRepositoryPort) {
        return new ExerciseService(aiAnalyzerPort, exerciseRepositoryPort);
    }
}
