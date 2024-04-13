package com.magnuspaal.messagingservice.chat.chatsettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SQLRestriction("deleted_at IS NULL")
public class ChatSettings extends BaseEntity {
  @Id
  @SequenceGenerator(name = "chat_settings_sequence", sequenceName = "chat_settings_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_settings_sequence")
  private Long id;

  @OneToOne
  @JoinColumn(name = "chat_id")
  @JsonIgnore
  private Chat chat;

  private String myChatBubbleColor;
  private String myChatTextColor;

  private String theirChatBubbleColor;
  private String theirChatTextColor;

  private String backgroundColor;

  private String textColor;

  private String elementColor;
  private String elementTextColor;

  public ChatSettings(Chat chat) {
    this.chat = chat;
    this.myChatBubbleColor = "#000000";
    this.myChatTextColor = "#FFFFFF";
    this.theirChatBubbleColor = "#000000";
    this.theirChatTextColor = "#FFFFFF";
    this.backgroundColor = "#FFFFFF";
    this.textColor = "#000000";
    this.elementColor = "#000000";
    this.elementTextColor = "#FFFFFF";
  }
}
