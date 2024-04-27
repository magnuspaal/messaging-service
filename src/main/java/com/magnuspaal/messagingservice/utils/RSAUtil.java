package com.magnuspaal.messagingservice.utils;

import com.magnuspaal.messagingservice.userencryption.UserEncryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

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

  public static PublicKey getPublicKey(UserEncryption userEncryption) {
    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(userEncryption.getPublicKey());
      return keyFactory.generatePublic(publicKeySpec);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
