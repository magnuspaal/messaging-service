package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.message.Message;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final UserService userService;

  @GetMapping("/chat/{id}/messages")
  public ResponseEntity<List<Message>> getChatMessages(@PathVariable Long id, @RequestParam Integer limit, @RequestParam Integer offset) {
    Chat chat = chatService.getChatById(id);
    return ResponseEntity.ok(messageService.getChatMessages(chat, limit, offset));
  }

  @PostMapping("/chat")
  public ResponseEntity<Chat> createChat(@RequestParam List<Long> userIds) {
    List<User> users = new ArrayList<>();
    userIds.stream().forEach(userId -> {
      users.add(userService.getUserById(userId));
    });
    Chat chat = chatService.createChat(new Chat(users));
    return ResponseEntity.ok(chat);
  }
}
