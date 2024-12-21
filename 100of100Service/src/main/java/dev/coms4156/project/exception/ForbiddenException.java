package dev.coms4156.project.exception;

/**
 * This service exception is thrown when a request is forbidden.
 */
public class ForbiddenException extends RuntimeException {

  public ForbiddenException() {
    super("Request is forbidden.");
  }

  public ForbiddenException(String message) {
    super(message);
  }
}
