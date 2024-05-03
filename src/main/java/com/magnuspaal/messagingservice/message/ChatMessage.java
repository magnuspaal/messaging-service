package com.magnuspaal.messagingservice.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.image.ChatImage;
import com.magnuspaal.messagingservice.messagereaction.MessageReaction;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chat_message")
@SQLRestriction("deleted_at IS NULL")
public class ChatMessage extends BaseEntity  {
  @Id
  @SequenceGenerator(name = "message_sequence", sequenceName = "message_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_sequence")
  private Long id;

  @NotNull
  private Long chatMessageId;

  private Long userEncryptionVersion;

  @ManyToOne
  @JoinColumn(name="owner_id")
  private User owner;

  @ManyToOne
  @JoinColumn(name="sender_id")
  private User sender;

  private byte[] content;

  private String type;

  @ManyToOne
  @JoinColumn(name="chat_id")
  @JsonIgnore
  private Chat chat;

  @Transient
  private Long chatId;

  @OneToOne
  @JoinColumn(name = "image_id", referencedColumnName = "id")
  private ChatImage chatImage;

  @OneToMany(mappedBy = "chatMessage", fetch = FetchType.EAGER)
  private List<MessageReaction> messageReactions = new ArrayList<>();

  @PostLoad
  private void postLoad() {
    this.chatId = chat.getId();
  }

  // TEXT
  public ChatMessage(@NotNull Long chatMessageId, Long userEncryptionVersion, byte[] content, User sender, User owner, Chat chat) {
    this.chatMessageId = chatMessageId;
    this.userEncryptionVersion = userEncryptionVersion;
    this.type = ChatMessageType.text.toString();
    this.content = content;
    this.sender = sender;
    this.owner = owner;
    this.chat = chat;
  }

  // IMAGE
  public ChatMessage(@NotNull Long chatMessageId, ChatImage chatImage, User sender, User owner, Chat chat) {
    this.chatMessageId = chatMessageId;
    this.chatImage = chatImage;
    this.type = ChatMessageType.image.toString();
    this.sender = sender;
    this.owner = owner;
    this.chat = chat;
    this.chatId = chat.getId();
  }

  // Reaction
  public ChatMessage(Long id, Long chatMessageId, User sender, Chat chat, String reaction, LocalDateTime createdAt) {
    this.id = id;
    this.type = ChatMessageType.reaction.toString();
    this.chatMessageId = chatMessageId;
    this.sender = sender;
    this.chat = chat;
    this.chatId = chat.getId();
    this.content = reaction.getBytes();
    this.setCreatedAt(createdAt);
  }

  public ChatMessage(ChatMessageExceptionMessage content, ChatMessageType type, User sender, User owner, Chat chat) {
    this.content = content.toString().getBytes(StandardCharsets.UTF_8);
    this.type = type.toString();
    this.sender = sender;
    this.owner = owner;
    this.chat = chat;
  }

  public ChatMessage(ChatMessageType type, User sender, Chat chat) {
    this.type = type.toString();
    this.sender = sender;
    this.chat = chat;
    this.chatId = chat.getId();
  }

  public ChatMessage(ChatMessageType type, User sender, Chat chat, String content) {
    this.type = type.toString();
    this.sender = sender;
    this.chat = chat;
    this.chatId = chat.getId();
    this.content = content.getBytes();
  }

  public ChatMessage(ChatMessageType type, User sender, User owner) {
    this.type = type.toString();
    this.sender = sender;
    this.owner = owner;
  }
}
