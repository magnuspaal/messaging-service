package com.magnuspaal.messagingservice.userencryption;

import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.utils.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@Service
@RequiredArgsConstructor
public class UserEncryptionService {

  private final UserEncryptionRepository userEncryptionRepository;

  public UserEncryption getUserEncryption(User user) throws NoUserEncryptionException {
    return userEncryptionRepository.findUserEncryption(user).orElseThrow(NoUserEncryptionException::new);
  }

  public UserEncryption getUserEncryption(User user, String password) {
    return userEncryptionRepository.findUserEncryption(user).orElseGet(() -> {
      byte[] salt = new byte[64];
      new SecureRandom().nextBytes(salt);
      return initializeUserEncryption(user, password, salt);
    });
  }

  private UserEncryption initializeUserEncryption(User user, String password, byte[] salt) {
    byte[] encryptedPrivateKey;
    IvParameterSpec iv;

    UserEncryption userEncryption;

    try {
      SecretKey key = AESUtil.getKeyFromPassword(password, salt);

      KeyPair keyPair = generateKeyPair();
      iv = AESUtil.generateIv();

      encryptedPrivateKey = AESUtil.encrypt(keyPair.getPrivate().getEncoded(), key, iv);

      userEncryption = new UserEncryption(
          user,
          1L,
          keyPair.getPublic().getEncoded(),
          encryptedPrivateKey,
          salt,
          iv.getIV()
      );

      userEncryption = userEncryptionRepository.save(userEncryption);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
             InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
             IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }

    return userEncryption;
  }

  private KeyPair generateKeyPair() {
    KeyPairGenerator generator;
    try {
      generator = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    generator.initialize(2048);
    return generator.generateKeyPair();
  }
}
