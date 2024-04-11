package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.message.dto.MessageRequest;
import com.magnuspaal.messagingservice.messaging.MessagingHandler;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessagingController {

  private final ChatService chatService;
  private final UserService userService;
  private final MessagingHandler messagingHandler;

  @MessageMapping("/message")
  @SendTo("/topic/message")
  public void getMessage(@Payload MessageRequest messageRequest, Principal principal) {
    if (principal.getName().equals(messageRequest.getFrom())) {
      User sender = userService.getUserById(Long.parseLong(principal.getName()));
      Chat chat = chatService.getChatById(Long.parseLong(messageRequest.getTo()));
      String content = messageRequest.getContent();

      switch (messageRequest.getType()) {
        case text -> messagingHandler.handleTextMessage(chat, sender, content);
        case writing -> messagingHandler.handleWritingMessage(chat, sender);
        case writing_end -> messagingHandler.handleWritingEndMessage(chat, sender);
        case seen -> messagingHandler.handleSeenMessage(sender, chat, Long.parseLong(content));
      }
    }
  }
}
