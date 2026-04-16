package com.edulive.infrastructure.adapter.out.ai;

import com.edulive.domain.model.Exercise;
import com.edulive.domain.port.out.AIAnalyzerPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.Base64;

@Component
public class SpringAIAnalyzerAdapter implements AIAnalyzerPort {

    private final ChatClient chatClient;

    // Extraemos el prompt a una constante para mantener el método limpio
    private static final String SYSTEM_PROMPT_TEMPLATE =
            "Eres un profesor experto en %s. Analiza la imagen adjunta que representa el tablero del estudiante. " +
                    "Corrige el ejercicio y explica los errores de forma constructiva y clara.";

    public SpringAIAnalyzerAdapter(ChatClient.Builder chatClientBuilder) {
        // Inicializamos el ChatClient. Las configuraciones (modelo, temperatura)
        // se inyectarán automáticamente desde el application.yml
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String analyze(Exercise exercise) {
        String instruction = String.format(SYSTEM_PROMPT_TEMPLATE, exercise.getSubject());
        ByteArrayResource imageResource = extractImageResource(exercise.getBase64Image());

        // Usamos el API fluido (Fluent API) de Spring AI. Es más legible y moderno.
        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(instruction)
                        .media(new Media(MimeTypeUtils.IMAGE_PNG, imageResource))
                )
                .call()
                .content();
    }

    /**
     * Método privado que respeta el Principio de Responsabilidad Única (SRP).
     * Solo se encarga de sanear y decodificar la imagen, manejando posibles errores.
     */
    private ByteArrayResource extractImageResource(String base64Data) {
        if (base64Data == null || base64Data.isBlank()) {
            throw new IllegalArgumentException("La imagen del ejercicio no puede estar vacía");
        }

        try {
            // Limpieza del prefijo web si existe
            String cleanBase64 = base64Data.contains(",") ? base64Data.split(",")[1] : base64Data;
            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
            return new ByteArrayResource(imageBytes);

        } catch (IllegalArgumentException e) {
            // Un Senior siempre captura errores de conversión para no tumbar la app
            throw new IllegalArgumentException("El formato de la imagen Base64 es inválido", e);
        }
    }
}