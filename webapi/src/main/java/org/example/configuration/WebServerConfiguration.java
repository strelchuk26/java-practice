package org.example.configuration;

import lombok.AllArgsConstructor;
import org.example.storage.StorageProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class WebServerConfiguration implements WebMvcConfigurer {
    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        String location = storageProperties.getLocation();
        registry.addResourceHandler("/"+location+"/**")
                .addResourceLocations("file:"+location+"/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600*1024);
    }
}
