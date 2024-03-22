package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.auth.AuthenticationService;
import com.magnuspaal.messagingservice.controllers.dto.UserEncryptionRequestDTO;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.userencryption.UserEncryption;
import com.magnuspaal.messagingservice.userencryption.UserEncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encryption")
@RequiredArgsConstructor
public class UserEncryptionController {

  private final UserEncryptionService userEncryptionService;
  private final AuthenticationService authenticationService;
  @PostMapping()
  public ResponseEntity<UserEncryption> getUserEncryption(@RequestBody UserEncryptionRequestDTO body) {
    User user = authenticationService.getAuthenticatedUser();
    UserEncryption userEncryption = userEncryptionService.getUserEncryption(user, body.password());
    return ResponseEntity.ok(userEncryption);
  }
}
