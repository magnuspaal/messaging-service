package com.magnuspaal.messagingservice.user;

import com.magnuspaal.messagingservice.chat.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepostitory userRepository;

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("user not found"));
  }

  public List<Chat> getUserChats(Long id) {
    User user = this.getUserById(id);
    return user.getChats();
  }

  public User updateOrCreateUser(User newUser) {
    User user = userRepository.findById(newUser.getId()).orElse(null);
    if (user == null) {
      return userRepository.save(newUser);
    } else {
      user.setEmail(newUser.getEmail());
      user.setFirstName(newUser.getFirstName());
      user.setLastName(newUser.getLastName());
      user.setUsername(newUser.getUsername());
      return userRepository.save(user);
    }
  }
}
