package com.magnuspaal.messagingservice.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.chatsettings.ChatSettings;
import com.magnuspaal.messagingservice.chatuser.ChatUser;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SQLRestriction("deleted_at IS NULL")
@ToString
public class Chat extends BaseEntity {
  @Id
  @SequenceGenerator(name = "chat_sequence", sequenceName = "chat_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_sequence")
  private Long id;

  @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
  private List<ChatUser> chatUsers;

  @JsonIgnore
  @OneToMany(mappedBy = "chat")
  @ToString.Exclude
  private List<ChatMessage> messages;

  @Transient
  private ChatMessage latestMessage;

  @OneToOne(mappedBy = "chat")
  private ChatSettings chatSettings;

  public Chat(List<User> users) {
    this.chatUsers = new ArrayList<>();
    for(User user: users) {
      this.chatUsers.add(new ChatUser(user, this));
    }
  }

  @JsonIgnore
  public List<User> getUsers() {
    List<User> users = new ArrayList<>();
    for (ChatUser chatUser: this.chatUsers) {
      users.add(chatUser.getUser());
    }
    return users;
  }
}
