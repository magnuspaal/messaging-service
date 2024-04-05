package com.magnuspaal.messagingservice.chatuser.seenmessagesrange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chatuser.ChatUser;
import com.magnuspaal.messagingservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "seen_messages_range")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@ToString
public class SeenMessagesRange extends BaseEntity {
  @Id
  @SequenceGenerator(name = "seen_messages_sequence", sequenceName = "seen_messages_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seen_messages_sequence")
  private Long id;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name="user_id", referencedColumnName="user_id"),
      @JoinColumn(name="chat_id", referencedColumnName="chat_id")
  })
  @JsonIgnore
  private ChatUser chatUser;

  private Long rangeStart;
  private Long rangeEnd;

  public SeenMessagesRange(ChatUser chatUser, Long rangeStart, Long rangeEnd) {
    this.chatUser = chatUser;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
  }
}
