package com.magnuspaal.messagingservice.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.Message;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_data")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

  @Id
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String username;

  @JsonIgnore
  @ManyToMany(mappedBy = "users")
  private List<Chat> chats;

  @JsonIgnore
  @OneToMany(mappedBy = "user")
  private List<Message> messages;

  public User(Long id, String firstName, String lastName, String email, String username) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
  }
}
