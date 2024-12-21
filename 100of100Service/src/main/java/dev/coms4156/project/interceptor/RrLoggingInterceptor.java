package dev.coms4156.project.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * This class logs the incoming requests and their completion status.
 * [RrLoggingInterceptor] stands for Request-Response Logging Interceptor.
 * Citation:
 * I used the following tutorial to learn how to create SpringBoot interceptors:
 * <a href="https://www.tutorialspoint.com/spring_boot/spring_boot_interceptor.htm">interceptor</a>
 */
@Component
public class RrLoggingInterceptor  implements HandlerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(RrLoggingInterceptor.class);

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) {
    String remoteAddr = request.getRemoteAddr();
    String remotePort = String.valueOf(request.getRemotePort());
    String method = request.getMethod();
    String requestUri = request.getRequestURI();
    logger.info("Request from {}:{} to {} '{}'...", remoteAddr, remotePort, method, requestUri);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex
  ) {
    String remoteAddr = request.getRemoteAddr();
    String remotePort = String.valueOf(request.getRemotePort());
    String method = request.getMethod();
    String requestUri = request.getRequestURI();
    String status = String.valueOf(response.getStatus());
    logger.info("Request from {}:{} to {} '{}' completed with status {}.",
        remoteAddr, remotePort, method, requestUri, status);
  }

}
