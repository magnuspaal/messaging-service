package com.magnuspaal.messagingservice.message;

import com.magnuspaal.messagingservice.chat.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;

  public ChatMessage createMessage(ChatMessage message) {
    return messageRepository.save(message);
  }

  public List<ChatMessage> getChatMessages(Chat chat, Integer limit, Integer offset) {
    return messageRepository.findChatMessages(chat, limit, offset).orElse(new ArrayList<>());
  }
}
