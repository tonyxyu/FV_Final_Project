package dev.coms4156.project.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for generating API keys.
 */
public final class ApiKeyGenerator {
  private static final String SECRET = "100of100-secret";

  private ApiKeyGenerator() {
    // Prevent instantiation
  }

  /**
   * Generates an API key for the given client ID.
   *
   * @param cid the client ID
   * @return the generated API key
   */
  public static String generateApiKey(String cid) {
    String rawKey = cid + SECRET;

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generating API key", e);
    }
  }
}
