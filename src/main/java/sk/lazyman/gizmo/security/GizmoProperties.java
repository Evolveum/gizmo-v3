package sk.lazyman.gizmo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author lazyman
 */
@Configuration
@PropertySources(value = {
        @PropertySource("classpath:gizmo-defaults.properties"),
        @PropertySource(value = "file:/opt/gizmo.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:./gizmo.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:../conf/gizmo.properties", ignoreResourceNotFound = true)
})
public class GizmoProperties {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
