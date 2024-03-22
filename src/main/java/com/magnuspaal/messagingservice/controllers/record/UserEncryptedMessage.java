package com.magnuspaal.messagingservice.controllers.record;

import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.userencryption.UserEncryption;
import lombok.Data;


@Data
public class UserEncryptedMessage {
  private User user;
  private UserEncryption userEncryption;
  private byte[] encryptedMessage;

  public UserEncryptedMessage(User user, UserEncryption userEncryption) {
    this.user = user;
    this.userEncryption = userEncryption;
  }
}
