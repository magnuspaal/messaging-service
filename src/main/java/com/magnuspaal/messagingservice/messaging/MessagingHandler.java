package com.magnuspaal.messagingservice.messaging;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.controllers.record.UserEncryptedMessage;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.message.ChatMessageExceptionMessage;
import com.magnuspaal.messagingservice.message.ChatMessageType;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.userencryption.UserEncryption;
import com.magnuspaal.messagingservice.userencryption.UserEncryptionService;
import com.magnuspaal.messagingservice.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingHandler {

  private final UserEncryptionService userEncryptionService;
  private final SimpMessagingTemplate template;
  private final MessageService messageService;

  public void handleTextMessage(Chat chat, User sender, String content) {
    Long chatMessageId = messageService.getChatMessageCount(chat) + 1;

    List<UserEncryptedMessage> userEncryptedMessages = new ArrayList<>();

    for (User chatUser: chat.getUsers()) {
      try {
        userEncryptedMessages.add(new UserEncryptedMessage(
            chatUser,
            userEncryptionService.getUserEncryption(chatUser)
        ));
      } catch (NoUserEncryptionException e) {
        ChatMessage errorMessage = new ChatMessage(ChatMessageExceptionMessage.no_user_encryption, ChatMessageType.exception, sender, chatUser, chat);
        template.convertAndSendToUser(sender.getId().toString(),"/topic/message", errorMessage);
        return;
      }
    }

    for (UserEncryptedMessage userEncryptedMessage: userEncryptedMessages) {
      PublicKey publicKey;
      try {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(userEncryptedMessage.getUserEncryption().getPublicKey());
        publicKey = keyFactory.generatePublic(publicKeySpec);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        throw new RuntimeException(e);
      }

      byte[] encryptedMessage = RSAUtil.encrypt(content, publicKey);
      userEncryptedMessage.setEncryptedMessage(encryptedMessage);
    }

    for (UserEncryptedMessage userEncryptedMessage: userEncryptedMessages) {
      UserEncryption userEncryption = userEncryptedMessage.getUserEncryption();
      byte[] encryptedMessage = userEncryptedMessage.getEncryptedMessage();
      User chatUser = userEncryptedMessage.getUser();

      ChatMessage message = messageService.createMessage(new ChatMessage(chatMessageId, userEncryption.getVersion(), encryptedMessage, sender, chatUser, chat));
      message.setChatId(chat.getId());
      template.convertAndSendToUser(chatUser.getId().toString(),"/topic/message", message);
    }
  }

  public void handleWritingMessage(Chat chat, User sender) {
    for (User chatUser: chat.getUsers()) {
      if (!chatUser.equals(sender)) {
        ChatMessage message = new ChatMessage(ChatMessageType.writing, sender, chat);
        template.convertAndSendToUser(chatUser.getId().toString(),"/topic/message", message);
      }
    }
  }

  public void handleWritingEndMessage(Chat chat, User sender) {
    for (User chatUser: chat.getUsers()) {
      if (!chatUser.equals(sender)) {
        ChatMessage message = new ChatMessage(ChatMessageType.writing_end, sender, chat);
        template.convertAndSendToUser(chatUser.getId().toString(),"/topic/message", message);
      }
    }
  }
}
