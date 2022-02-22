package org.moonlight.idempotent.config;

import org.moonlight.idempotent.interceptor.IdempotentInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Moonlight
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final IdempotentInterceptor idempotentInterceptor;

    public WebConfiguration(IdempotentInterceptor idempotentInterceptor) {
        this.idempotentInterceptor = idempotentInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(idempotentInterceptor);
    }
}