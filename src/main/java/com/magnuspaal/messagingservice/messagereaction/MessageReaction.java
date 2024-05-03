package com.magnuspaal.messagingservice.messagereaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.ChatMessage;
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
@Table(name = "message_reaction")
@SQLRestriction("deleted_at IS NULL")
public class MessageReaction extends BaseEntity {
  @Id
  @SequenceGenerator(name = "message_reaction_sequence", sequenceName = "message_reaction_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_reaction_sequence")
  private Long id;

  @ManyToOne
  @JoinColumn(name="chat_message_id")
  @JsonIgnore
  private ChatMessage chatMessage;

  private String reaction;

  @ManyToOne
  @JoinColumn(name="user_id")
  private User user;

  public MessageReaction(ChatMessage chatMessage, String reaction, User sender) {
    this.chatMessage = chatMessage;
    this.reaction = reaction;
    this.user = sender;
  }
}
