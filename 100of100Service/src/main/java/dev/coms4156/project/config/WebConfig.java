package dev.coms4156.project.config;

import dev.coms4156.project.interceptor.ApiKeyInterceptor;
import dev.coms4156.project.interceptor.ParameterDecodingInterceptor;
import dev.coms4156.project.interceptor.RrLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class configures the web settings for the system.
 * Design under the Chain of Responsibility pattern.
 * Citation:
 * I used the following tutorial to learn how to create SpringBoot interceptors:
 * <a href="https://www.tutorialspoint.com/spring_boot/spring_boot_interceptor.htm">interceptor</a>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Autowired
  private RrLoggingInterceptor rrLoggingInterceptor;

  @Autowired
  private ParameterDecodingInterceptor parameterDecodingInterceptor;

  @Autowired
  private ApiKeyInterceptor apiKeyInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rrLoggingInterceptor).addPathPatterns("/**");
    registry.addInterceptor(apiKeyInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/register"
        );
    registry.addInterceptor(parameterDecodingInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/login",
            "/register"
        );
  }

  /**
   * This method configures the CORS settings so that the frontend can access the backend.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "PATCH", "POST", "PUT", "DELETE")
        .allowedHeaders("*");
  }
}
