package com.edulive.infrastructure.config;

import com.edulive.infrastructure.security.JwtRestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtRestInterceptor jwtRestInterceptor;

    @Autowired
    public WebMvcConfig(JwtRestInterceptor jwtRestInterceptor) {
        this.jwtRestInterceptor = jwtRestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply the interceptor to specific API paths
        registry.addInterceptor(jwtRestInterceptor)
                .addPathPatterns("/api/v1/**")
                // Exclude paths that don't need authentication like Swagger or public health checks
                .excludePathPatterns("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");
    }
}
