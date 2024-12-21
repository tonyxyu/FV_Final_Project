package dev.coms4156.project;

import dev.coms4156.project.utils.CodecUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * A unit test class for the Codec Utils class.
 */
@SpringBootTest
public class CodecUtilsTest {

  @Test
  public void testDecode() {
    String encoded = "YzVlZWVmNjc";
    String decoded = CodecUtils.decode(encoded);
    Assertions.assertEquals("c5eeef67", decoded);
  }

  @Test
  public void testEncode() {
    String decoded = "987654321";
    String encoded = CodecUtils.encode(decoded);
    Assertions.assertEquals("OTg3NjU0MzIx", encoded);
  }

  @Test
  public void testDecodeInvalid() {
    String encoded = "1";
    Assertions.assertThrows(IllegalArgumentException.class, () -> CodecUtils.decode(encoded));
  }


}
