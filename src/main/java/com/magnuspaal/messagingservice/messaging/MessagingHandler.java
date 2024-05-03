package com.magnuspaal.messagingservice.messaging;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chatuser.ChatUser;
import com.magnuspaal.messagingservice.chatuser.ChatUserService;
import com.magnuspaal.messagingservice.chatuser.seenmessagesrange.SeenMessagesRange;
import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.controllers.record.UserEncryptedMessage;
import com.magnuspaal.messagingservice.image.ChatImage;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.message.ChatMessageExceptionMessage;
import com.magnuspaal.messagingservice.message.ChatMessageType;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.messagereaction.MessageReaction;
import com.magnuspaal.messagingservice.messagereaction.MessageReactionService;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessagingHandler {

  private final UserEncryptionService userEncryptionService;
  private final SimpMessagingTemplate template;
  private final MessageService messageService;
  private final ChatUserService chatUserService;
  private final UserService userService;
  private final MessageReactionService messageReactionService;

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

  public void handleSeenMessage(User sender, Chat chat, Long chatMessageId) {
    ChatUser chatUser = chatUserService.getChatUserFromChat(chat, sender);
    SeenMessagesRange currentRange = new SeenMessagesRange(chatUser, chatMessageId, chatMessageId);
    chatUserService.updateSeenMessagesRanges(chatUser, currentRange);
    sendSeenMessage(sender, chat, chatMessageId);
  }

  public void handleActiveMessage(User sender, boolean isConnectMessage) {
    List<Chat> chats = userService.getUserChats(sender.getId());

    Set<User> users = new HashSet<>();

    for (Chat chat: chats) {
      users.addAll(chat.getUsers());
    }

    for (User user: users) {
      if (!sender.equals(user)) {
        ChatMessage message = new ChatMessage(isConnectMessage ? ChatMessageType.connect : ChatMessageType.active, sender, user);
        template.convertAndSendToUser(user.getId().toString(),"/topic/message", message);
      }
    }
  }

  public void sendSeenMessage(User sender, Chat chat, Long chatMessageId) {
    for (User user: chat.getUsers()) {
      if (!user.equals(sender)) {
        ChatMessage message = new ChatMessage(ChatMessageType.seen, sender, chat, chatMessageId.toString());
        template.convertAndSendToUser(user.getId().toString(),"/topic/message", message);
      }
    }
  }

  public void handleImageMessage(Chat chat, User sender, User user, ChatImage chatImage, Long chatMessageId) {
    ChatMessage message = messageService.createMessage(new ChatMessage(chatMessageId, chatImage, sender, user, chat));
    template.convertAndSendToUser(user.getId().toString(),"/topic/message", message);
  }

  public void handleReactionMessage(Chat chat, Long chatMessageId, User sender, String reaction) {
    List<ChatMessage> chatMessages = messageService.getChatMessage(chat, chatMessageId);
    for (ChatMessage chatMessage: chatMessages) {
       MessageReaction messageReaction = messageReactionService.createMessageReaction(new MessageReaction(chatMessage, reaction, sender));
       ChatMessage message = new ChatMessage(
           messageReaction.getId(),
           chatMessageId,
           sender,
           chat,
           messageReaction.getReaction(),
           messageReaction.getCreatedAt()
       );
       template.convertAndSendToUser(String.valueOf(chatMessage.getOwner().getId()),"/topic/message", message);
    }
  }
}
