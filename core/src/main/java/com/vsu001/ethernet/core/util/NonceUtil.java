package com.vsu001.ethernet.core.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class NonceUtil {

  /**
   * Generate a random string (hexadecimal encoding) for the purpose of being used only once.
   *
   * @return Hexadecimal encoded random string.
   */
  public static String generateNonce() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] token = new byte[16];
    secureRandom.nextBytes(token);
    return new BigInteger(1, token).toString(16); // Hexadecimal encoding
  }

}
