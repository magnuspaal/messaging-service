package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.auth.AuthenticationService;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthenticationService authenticationService;

  @GetMapping("/user/{id}/chats")
  public ResponseEntity<List<Chat>> getUserChats(@PathVariable("id") Long userId) {
    User authenticatedUser = authenticationService.getAuthenticatedUser();
    if (!Objects.equals(authenticatedUser.getId(), userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    List<Chat> chats = userService.getUserChats(userId);
    chats.forEach(chat -> {
      List<User> filteredUsers = chat.getUsers().stream().filter(user -> !Objects.equals(user.getId(), userId)).toList();
      chat.setUsers(filteredUsers);
    });
    return ResponseEntity.ok(chats);
  }
}
