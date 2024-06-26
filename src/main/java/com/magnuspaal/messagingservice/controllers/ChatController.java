package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.auth.AuthenticationService;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.chat.chatsettings.ChatSettings;
import com.magnuspaal.messagingservice.chat.chatsettings.ChatSettingsRepository;
import com.magnuspaal.messagingservice.chatuser.ChatUser;
import com.magnuspaal.messagingservice.chatuser.ChatUserService;
import com.magnuspaal.messagingservice.controllers.dto.UpdateChatSettingsDTO;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.messaging.MessagingHandler;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import com.magnuspaal.messagingservice.controllers.dto.CreateChatDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final UserService userService;
  private final AuthenticationService authenticationService;
  private final ChatUserService chatUserService;
  private final MessagingHandler messagingHandler;
  private final ChatSettingsRepository chatSettingsRepository;

  @GetMapping("/{id}")
  public ResponseEntity<Chat> getChat(@PathVariable Long id) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    Chat chat = chatService.getChatById(id);
    if (!chat.getUsers().contains(authenticatedUser)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(chat);
  }

  @GetMapping("/{id}/messages")
  public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Long id, @RequestParam Integer limit, @RequestParam Integer offset) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    Chat chat = chatService.getChatById(id);

    ChatUser chatUser = chatUserService.getChatUserFromChat(chat, authenticatedUser);

    if (chatUser == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<ChatMessage> chatMessages = messageService.getChatMessages(authenticatedUser, chat, limit, offset);

    chatUserService.updateSeenMessagesRangesFromChatMessages(chatUser, chatMessages);

    chatMessages.stream()
        .max(Comparator.comparing(ChatMessage::getChatMessageId))
        .ifPresent(latestMessage -> {
          if (latestMessage.getChatMessageId() >= chatUser.getLatestSeenMessage()) {
            messagingHandler.sendSeenMessage(authenticatedUser, chat, latestMessage.getChatMessageId());
          }
        });

    return ResponseEntity.ok(chatMessages);
  }

  @PatchMapping("/{id}/settings")
  public ResponseEntity<ChatSettings> updateChatSettings(@PathVariable Long id, @RequestBody UpdateChatSettingsDTO body) {
    Chat chat = chatService.getChatById(id);
    ChatSettings chatSettings = chat.getChatSettings();

    if (chatSettings == null) {
      chatSettings = new ChatSettings(chat);
    }

    try {
      Class<?> internClass= ChatSettings.class;
      Field[] internFields=internClass.getDeclaredFields();

      for(Field field : internFields){
        field.setAccessible(true);

        Object value=field.get(body.getChatSettings());
        if(value!=null){
          field.set(chatSettings,value);
        }
        field.setAccessible(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    ChatSettings returnedChatSettings = chatSettingsRepository.save(chatSettings);
    return ResponseEntity.ok(returnedChatSettings);
  }

  @PostMapping()
  public ResponseEntity<Chat> createChat(@RequestBody CreateChatDTO body) {
    List<User> dbUsers = new ArrayList<>();
    body.getUsers().forEach(user -> {
      try {
        dbUsers.add(userService.getUserById(user.getId()));
      } catch (NoSuchElementException exception) {
        dbUsers.add(userService.updateOrCreateUser(new User(user.getId(), "", "", "", user.getUsername())));
      }
    });
    Chat chat = chatService.createChat(new Chat(dbUsers));
    return ResponseEntity.ok(chat);
  }
}
