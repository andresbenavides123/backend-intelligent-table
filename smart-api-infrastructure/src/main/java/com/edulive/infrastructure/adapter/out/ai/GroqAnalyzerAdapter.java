package com.edulive.infrastructure.adapter.out.ai;

import com.edulive.domain.model.Exercise;
import com.edulive.domain.port.out.AIAnalyzerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

/**
 * Implementación del puerto de análisis de IA usando Groq con el modelo Llama 4 Scout.
 *
 * <p>Groq expone una API compatible con OpenAI, por lo que usamos {@link ChatClient}
 * del starter {@code spring-ai-openai} apuntando al base-url de Groq definido
 * en {@code application.yml}.</p>
 *
 * <p>Llama 4 Scout es un modelo multimodal: acepta imágenes en base64 dentro
 * del campo {@code image_url} del mensaje de usuario.</p>
 */
@Component
public class GroqAnalyzerAdapter implements AIAnalyzerPort {

    private static final Logger log = LoggerFactory.getLogger(GroqAnalyzerAdapter.class);

    private static final String SYSTEM_PROMPT =
            "Eres un profesor experto y cercano. Tu tarea es analizar el ejercicio " +
            "del estudiante que aparece en la imagen adjunta y dar retroalimentación " +
            "clara, constructiva y motivadora. Identifica errores, explica por qué " +
            "están mal y sugiere cómo mejorar. Responde siempre en español.";

    private final ChatClient chatClient;

    /**
     * El {@link ChatClient.Builder} es inyectado automáticamente por Spring AI.
     * El modelo y la URL ya están configurados en {@code application.yml}.
     */
    public GroqAnalyzerAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    @Override
    public String analyze(Exercise exercise) {
        validateExercise(exercise);

        byte[] imageBytes = extractImageBytes(exercise.getBase64Image());
        String userPrompt = buildUserPrompt(exercise.getSubject());

        log.info("Sending request to Groq (Llama 4 Scout) — subject: '{}', image size: {} bytes",
                exercise.getSubject(), imageBytes.length);

        // Spring AI OpenAI adapter acepta Media con bytes para construir el image_url base64
        Media imageMedia = Media.builder()
                .mimeType(MimeTypeUtils.IMAGE_PNG)
                .data(imageBytes)
                .build();

        try {
            String response = chatClient.prompt()
                    .user(userSpec -> userSpec
                            .text(userPrompt)
                            .media(imageMedia)
                    )
                    .call()
                    .content();

            log.info("Groq response received — {} characters", response != null ? response.length() : 0);
            return response;

        } catch (Exception e) {
            log.error("Groq API call failed — model: meta-llama/llama-4-scout-17b-16e-instruct, error: {}",
                    e.getMessage(), e);
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // Métodos privados (SRP: cada uno tiene una responsabilidad única)
    // -------------------------------------------------------------------------

    /**
     * Construye el prompt contextualizado con la materia del estudiante.
     */
    private String buildUserPrompt(String subject) {
        return String.format(
                "Por favor analiza el ejercicio de la imagen. La materia es: %s. " +
                "Dame una corrección detallada con los errores encontrados y " +
                "cómo mejorar.", subject);
    }

    /**
     * Valida que el ejercicio tenga los datos mínimos requeridos.
     */
    private void validateExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("El ejercicio no puede ser nulo");
        }
        if (exercise.getSubject() == null || exercise.getSubject().isBlank()) {
            throw new IllegalArgumentException("La materia del ejercicio no puede estar vacía");
        }
        if (exercise.getBase64Image() == null || exercise.getBase64Image().isBlank()) {
            throw new IllegalArgumentException("La imagen del ejercicio no puede estar vacía");
        }
    }

    /**
     * Extrae y decodifica el string base64 a bytes crudos.
     * Soporta imágenes con prefijo Data-URL (ej: {@code data:image/png;base64,...}).
     */
    @NonNull
    private byte[] extractImageBytes(String base64Data) {
        try {
            String cleanBase64 = base64Data.contains(",")
                    ? base64Data.split(",", 2)[1]
                    : base64Data;

            return java.util.Base64.getDecoder().decode(cleanBase64.trim());

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato Base64 de la imagen es invalido", e);
        }
    }
}
