package com.edulive.domain.port.out;

import com.edulive.domain.model.Exercise;

public interface AIAnalyzerPort {
    // Recibe el ejercicio con la imagen, devuelve la corrección en texto
    String analyze(Exercise exercise);
}