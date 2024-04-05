package com.magnuspaal.messagingservice.chat;

import com.magnuspaal.messagingservice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

  public Chat getPrivateChat(User user1, User user2) {
    List<Chat> result = chatRepository.findPrivateChat(user1, user2).orElse(null);
    if (result == null) {
      return null;
    }
    for (Chat chat : result) {
      if (chat.getUsers().size() == 2) {
        return chat;
      }
    }
    return null;
  }
}
