package com.magnuspaal.messagingservice.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.MGF1ParameterSpec;

public class RSAUtil {
  public static byte[] encrypt(String message, Key publicKey) {
    Cipher encryptCipher;
    try {
      encryptCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
      OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
      encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
      byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
      return encryptCipher.doFinal(secretMessageBytes);
    } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
             InvalidKeyException | InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    }
  }
}
