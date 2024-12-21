package dev.coms4156.project.exception;

/**
 * An exception thrown when a bad request is made by the client.
 */
public class BadRequestException extends RuntimeException {
  public BadRequestException() {
    super("Bad request.");
  }

  public BadRequestException(String message) {
    super(message);
  }
}
