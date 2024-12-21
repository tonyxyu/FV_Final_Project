package dev.coms4156.project.exception;

/**
 * This service exception is thrown when an item is not found.
 */
public class NotFoundException extends RuntimeException {

  public NotFoundException() {
    super("Item not found.");
  }

  public NotFoundException(String message) {
    super(message);
  }
}
