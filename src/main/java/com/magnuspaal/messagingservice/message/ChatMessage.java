package com.magnuspaal.messagingservice.message;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chat_message")
@SQLRestriction("deleted_at IS NULL")
public class ChatMessage extends BaseEntity  {
  @Id
  @SequenceGenerator(
      name = "message_sequence",
      sequenceName = "message_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "message_sequence"
  )
  private Long id;
  private String content;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name="chat_id")
  private Chat chat;

  public ChatMessage(String content, User user, Chat chat) {
    this.content = content;
    this.user = user;
    this.chat = chat;
  }
}
