package com.magnuspaal.messagingservice.message.dto;

import com.magnuspaal.messagingservice.message.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
  private String content;
  private ChatMessageType type;
  private String from;
  private String to;
  private Long chatMessageId;
}