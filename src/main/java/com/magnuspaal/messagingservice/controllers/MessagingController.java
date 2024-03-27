package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.controllers.record.UserEncryptedMessage;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.message.ChatMessageExceptionMessage;
import com.magnuspaal.messagingservice.message.ChatMessageType;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.message.dto.MessageRequest;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import com.magnuspaal.messagingservice.userencryption.UserEncryption;
import com.magnuspaal.messagingservice.userencryption.UserEncryptionService;
import com.magnuspaal.messagingservice.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessagingController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final SimpMessagingTemplate template;
  private final UserService userService;
  private final UserEncryptionService userEncryptionService;

  @MessageMapping("/message")
  @SendTo("/topic/message")
  public void getMessage(@Payload MessageRequest messageRequest, Principal principal) {
    if (principal.getName().equals(messageRequest.getFrom())) {
      User sender = userService.getUserById(Long.parseLong(principal.getName()));

      Chat chat = chatService.getChatById(Long.parseLong(messageRequest.getTo()));

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

        byte[] encryptedMessage = RSAUtil.encrypt(messageRequest.getContent(), publicKey);
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
  }
}
