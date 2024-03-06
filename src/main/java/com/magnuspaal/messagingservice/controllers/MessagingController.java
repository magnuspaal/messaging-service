package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.message.Message;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.message.dto.MessageRequest;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessagingController {

  private final ChatService chatService;
  private final MessageService messageService;
  private final SimpMessagingTemplate template;
  private final UserService userService;

  @MessageMapping("/message")
  @SendTo("/topic/message")
  public Message getMessage(@Payload MessageRequest messageRequest, Principal principal) throws Exception {
    if (principal.getName().equals(messageRequest.getFrom())) {
      User user = userService.getUserById(Long.parseLong(principal.getName()));

      Chat chat = chatService.getChatById(Long.parseLong(messageRequest.getTo()));
      Message message = messageService.createMessage(new Message(messageRequest.getContent(), user, chat));

      chat.getUsers().forEach(chatUser -> {
        template.convertAndSendToUser(chatUser.getId().toString(),"/topic/message", message);
      });
    }
    return null;
  }
}
