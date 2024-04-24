package com.magnuspaal.messagingservice.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chatuser.ChatUser;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.ChatMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_data")
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity {

  @Id
  private Long id;
  @JsonIgnore
  private String firstName;
  @JsonIgnore
  private String lastName;
  @JsonIgnore
  private String email;
  private String username;

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private List<ChatUser> chatUsers;

  @JsonIgnore
  @OneToMany(mappedBy = "owner")
  private List<ChatMessage> messages;

  @JsonIgnore
  @OneToMany(mappedBy = "sender")
  private List<ChatMessage> sentMessages;

  public User(Long id, String firstName, String lastName, String email, String username) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
  }

  public boolean equals(Object user) {
    return this.id.equals(((User) user).getId());
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @JsonIgnore
  public List<Chat> getChats() {
    List<Chat> chats = new ArrayList<>();
    for (ChatUser chatUser: this.chatUsers) {
      chats.add(chatUser.getChat());
    }
    return chats;
  }
}
