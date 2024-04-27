package com.magnuspaal.messagingservice.image.dto;

import com.magnuspaal.messagingservice.image.ChatImage;
import com.magnuspaal.messagingservice.user.User;

public record UserChatImage(User user, ChatImage chatImage) {
}
