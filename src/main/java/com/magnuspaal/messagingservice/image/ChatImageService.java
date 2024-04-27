package com.magnuspaal.messagingservice.image;

import com.magnuspaal.messagingservice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatImageService {
  private final ChatImageRepository chatImageRepository;

  public ChatImage findChatImage(String filename, User user) {
    return chatImageRepository.findChatImage(filename, user).orElseThrow(() -> new NoSuchElementException("Chat image not found!"));
  }
}
