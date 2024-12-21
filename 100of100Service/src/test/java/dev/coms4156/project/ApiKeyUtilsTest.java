package dev.coms4156.project;

import dev.coms4156.project.utils.ApiKeyGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * A unit test class for the ApiKeyGenerator Utils class.
 */
@SpringBootTest
public class ApiKeyUtilsTest {

  @Test
  public void testGenerate() {
    String cid = "MQ";
    String apiKey = ApiKeyGenerator.generateApiKey(cid);
    String expectedApiKey = "FtOBd1dVudC3c1qq-DSJhjWR518GLghZkzB5gXclIuY";
    Assertions.assertEquals(expectedApiKey, apiKey);
    System.out.println(apiKey);
  }

}
