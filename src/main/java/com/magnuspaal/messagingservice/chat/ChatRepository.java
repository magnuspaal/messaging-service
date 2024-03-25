package com.magnuspaal.messagingservice.chat;

import com.magnuspaal.messagingservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

  @Query("SELECT c FROM Chat c JOIN c.users user1 JOIN c.users user2 WHERE user1=:user1 AND user2=:user2")
  Optional<List<Chat>> findPrivateChat(
      @Param("user1") User user1,
      @Param("user2") User user2
  );
}
