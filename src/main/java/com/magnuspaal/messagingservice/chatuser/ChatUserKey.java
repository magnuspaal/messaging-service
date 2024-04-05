package com.magnuspaal.messagingservice.chatuser;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUserKey implements Serializable {

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "chat_id")
  private Long chatId;
}
