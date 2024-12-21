package dev.coms4156.project.exception;

/**
 * An exception to represent an internal server error.
 */
public class InternalServerErrorException extends RuntimeException {

  public InternalServerErrorException() {
    super("Internal server error.");
  }

  public InternalServerErrorException(String message) {
    super(message);
  }
}
