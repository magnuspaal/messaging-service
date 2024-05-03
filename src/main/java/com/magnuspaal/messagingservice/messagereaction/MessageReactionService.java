package com.magnuspaal.messagingservice.messagereaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReactionService {

  private final MessageReactionRepository messageReactionRepository;

  public MessageReaction createMessageReaction(MessageReaction messageReaction) {
    return messageReactionRepository.save(messageReaction);
  }
}