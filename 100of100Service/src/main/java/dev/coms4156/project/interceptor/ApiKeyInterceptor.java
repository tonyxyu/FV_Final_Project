package dev.coms4156.project.interceptor;

import dev.coms4156.project.exception.BadRequestException;
import dev.coms4156.project.exception.ForbiddenException;
import dev.coms4156.project.utils.ApiKeyGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for validating API keys.
 */
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws IOException {
    String apiKey = request.getHeader("Authorization");
    String cid = request.getParameter("cid");

    if (apiKey == null || !apiKey.startsWith("LWXY")) {
      throw new BadRequestException("API key is required");
    }

    apiKey = apiKey.substring(4); // Remove "LWXY" prefix

    String expectedApiKey = ApiKeyGenerator.generateApiKey(cid);
    if (!apiKey.equals(expectedApiKey)) {
      throw new ForbiddenException("Invalid API key");
    }

    return true;
  }
}
