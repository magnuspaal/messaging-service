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

  public Message createMessage(Message message) {
    return messageRepository.save(message);
  }

  public List<Message> getChatMessages(Chat chat, Integer limit, Integer offset) {
    List<Message> messages = messageRepository.findChatMessages(chat, limit, offset).orElse(new ArrayList<>());
    return messages;
  }
}
