package com.magnuspaal.messagingservice.chatuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chatuser.seenmessagesrange.SeenMessagesRange;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "chat_user")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class ChatUser extends BaseEntity {
  @EmbeddedId
  private ChatUserKey id;

  @ManyToOne(fetch = FetchType.EAGER)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @MapsId("chatId")
  @JoinColumn(name = "chat_id")
  @JsonIgnore
  private Chat chat;

  @OneToMany(mappedBy = "chatUser", fetch = FetchType.EAGER)
  private List<SeenMessagesRange> seenMessagesRanges;

  @Transient
  private Long latestSeenMessage;

  public ChatUser(User user, Chat chat) {
    this.user = user;
    this.chat = chat;
  }

  @PostLoad
  private void postLoad() {
    long latestSeenMessage = 0;

    for (SeenMessagesRange range: seenMessagesRanges) {
      if (range.getRangeEnd() > latestSeenMessage) latestSeenMessage = range.getRangeEnd();
    }

    this.latestSeenMessage = latestSeenMessage;
  }
}
