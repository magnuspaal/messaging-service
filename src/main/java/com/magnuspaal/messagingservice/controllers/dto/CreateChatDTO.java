package com.magnuspaal.messagingservice.controllers.dto;

import com.magnuspaal.messagingservice.user.User;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatDTO {

  List<User> users;
}


