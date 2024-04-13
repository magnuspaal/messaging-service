package com.magnuspaal.messagingservice.controllers.dto;

import com.magnuspaal.messagingservice.chat.chatsettings.ChatSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChatSettingsDTO {
  private ChatSettings chatSettings;
}
