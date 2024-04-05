package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.auth.AuthenticationService;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import com.magnuspaal.messagingservice.userencryption.UserEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthenticationService authenticationService;
  private final ChatService chatService;
  private final UserEncryptionService userEncryptionService;
  private final MessageService messageService;

  @GetMapping("/chats")
  public ResponseEntity<List<Chat>> getUserChats() {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    Long userId = authenticatedUser.getId();
    List<Chat> chats = userService.getUserChats(userId);
    for (Chat chat: chats) {
      List<ChatMessage> messages = messageService.getChatMessages(authenticatedUser, chat, 1, 0);
      chat.setLatestMessage(messages.stream().findFirst().orElse(null));
    }
    return ResponseEntity.ok(chats);
  }

  @GetMapping("/{id}/chat")
  public ResponseEntity<Chat> getPrivateChat(@PathVariable Long id) throws NoUserEncryptionException {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    User user = userService.getUserById(id);
    userEncryptionService.getUserEncryption(user);
    Chat chat = chatService.getPrivateChat(authenticatedUser, user);
    if (chat == null) {
      return ResponseEntity.status(404).build();
    } else {
      return ResponseEntity.ok(chat);
    }
  }
}
