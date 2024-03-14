package com.magnuspaal.messagingservice.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SQLRestriction("deleted_at IS NULL")
public class Chat extends BaseEntity {
  @Id
  @SequenceGenerator(
      name = "chat_sequence",
      sequenceName = "chat_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "chat_sequence"
  )
  private Long id;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "chat_user",
      joinColumns = @JoinColumn(name = "chat_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> users;

  @JsonIgnore
  @OneToMany(mappedBy = "chat")
  private List<ChatMessage> message;

  public Chat(List<User> users) {
    this.users = users;
  }
}
