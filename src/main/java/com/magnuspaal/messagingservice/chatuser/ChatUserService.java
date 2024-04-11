package com.magnuspaal.messagingservice.chatuser;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chatuser.seenmessagesrange.SeenMessagesRange;
import com.magnuspaal.messagingservice.chatuser.seenmessagesrange.SeenMessagesRangeRepository;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatUserService {
  private final ChatUserRepository chatUserRepository;
  private final SeenMessagesRangeRepository seenMessagesRangeRepository;

  public ChatUser getChatUser(User user, Chat chat) {
    return chatUserRepository.findById(new ChatUserKey(user.getId(), chat.getId())).orElse(null);
  }

  public ChatUser getChatUserFromChat(Chat chat, User user) {
    List<ChatUser> chatUsers = chat.getChatUsers();
    return chatUsers.stream().filter((chatUser1 -> Objects.equals(chatUser1.getUser().getId(), user.getId()))).findFirst().orElse(null);
  }

  public void updateSeenMessagesRangesFromChatMessages(ChatUser chatUser, List<ChatMessage> chatMessages) {
    if (chatMessages.size() != 0) {
      SeenMessagesRange currentRange = findChatMessagesRange(chatUser, chatMessages);
      updateSeenMessagesRanges(chatUser, currentRange);
    }
  }

  public void updateSeenMessagesRanges(ChatUser chatUser, SeenMessagesRange currentRange) {
    List<SeenMessagesRange> ranges = chatUser.getSeenMessagesRanges();

    if (ranges == null || ranges.isEmpty()) {
      seenMessagesRangeRepository.save(currentRange);
      return;
    }

    List<SeenMessagesRange> foundRanges = new ArrayList<>();
    boolean foundExactMatch = false;

    for (SeenMessagesRange range: ranges) {
      RangeMatch rangeMatch = findIfRangesMatch(currentRange, range);
      if (rangeMatch.exactMatch) {
        foundExactMatch = true;
        break;
      } else if (rangeMatch.foundMatch) {
        foundRanges.add(rangeMatch.foundRange);
      }
    }

    if (!foundExactMatch) {
      saveRanges(foundRanges, currentRange);
    }
  }

  private SeenMessagesRange findChatMessagesRange(ChatUser chatUser, List<ChatMessage> chatMessages) {
    long rangeStart = Long.MAX_VALUE;
    long rangeEnd = 0L;
    for (ChatMessage message: chatMessages) {
      if (message.getChatMessageId() > rangeEnd) rangeEnd = message.getChatMessageId();
      if (message.getChatMessageId() < rangeStart) rangeStart = message.getChatMessageId();
    }
    return new SeenMessagesRange(chatUser, rangeStart, rangeEnd);
  }

  private RangeMatch findIfRangesMatch(SeenMessagesRange currentRange, SeenMessagesRange range) {
    Long currentRangeStart = currentRange.getRangeStart();
    Long currentRangeEnd = currentRange.getRangeEnd();

    Long rangeStart = range.getRangeStart();
    Long rangeEnd = range.getRangeEnd();

    if ((currentRangeStart < rangeStart && currentRangeEnd >= rangeStart && currentRangeEnd <= rangeEnd) || currentRangeEnd + 1 == rangeStart) {
      range.setRangeStart(currentRangeStart);
      return new RangeMatch(range);
    }
    else if ((currentRangeEnd > rangeEnd && currentRangeStart <= rangeEnd && currentRangeStart >= rangeStart) || currentRangeStart - 1 == rangeEnd) {
      range.setRangeEnd(currentRangeEnd);
      return new RangeMatch(range);
    }
    else if (currentRangeStart < rangeStart && currentRangeEnd > rangeEnd) {
      range.setRangeStart(currentRangeStart);
      range.setRangeEnd(currentRangeEnd);
      return new RangeMatch(range);
    } else if (currentRange.getRangeStart() >= range.getRangeStart() && currentRange.getRangeEnd() <= range.getRangeEnd()) {
      return new RangeMatch();
    } else {
      return new RangeMatch(false);
    }
  }

  private void mergeAndSaveOverlappingRanges(List<SeenMessagesRange> ranges) {
    long rangeStart = Long.MAX_VALUE;
    long rangeEnd = 0L;
    for (int i = 0; i < ranges.size(); i++) {
      SeenMessagesRange range = ranges.get(i);
      if (range.getRangeStart() < rangeStart) rangeStart = range.getRangeStart();
      if (range.getRangeEnd() > rangeEnd) rangeEnd = range.getRangeEnd();
      if (i == ranges.size() - 1) {
        range.setRangeStart(rangeStart);
        range.setRangeEnd(rangeEnd);
        seenMessagesRangeRepository.save(range);
      } else {
        seenMessagesRangeRepository.delete(range);
      }
    }
  }

  private void saveRanges(List<SeenMessagesRange> ranges, SeenMessagesRange currentRange) {
    if (ranges.size() == 0) {
      seenMessagesRangeRepository.save(currentRange);
    } else if (ranges.size() == 1) {
      seenMessagesRangeRepository.save(ranges.get(0));
    } else {
      mergeAndSaveOverlappingRanges(ranges);
    }
  }

  private static class RangeMatch {
    boolean exactMatch;
    boolean foundMatch;
    SeenMessagesRange foundRange;

    public RangeMatch() {
      this.exactMatch = true;
    }

    public RangeMatch(boolean foundMatch) {
      this.foundMatch = foundMatch;
    }

    public RangeMatch(SeenMessagesRange foundRange) {
      this.foundRange = foundRange;
      this.foundMatch = true;
      this.exactMatch = false;
    }
  }
}
