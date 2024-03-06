package com.magnuspaal.messagingservice.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRepository chatRepository;

  public Chat getChatById(Long id) {
    return chatRepository.findById(id).orElseThrow(() -> new NoSuchElementException("chat not found"));
  }

  public Chat createChat(Chat chat) {
    return chatRepository.save(chat);
  }
}
