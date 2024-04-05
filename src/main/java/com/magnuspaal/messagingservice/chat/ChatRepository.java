package com.magnuspaal.messagingservice.chat;

import com.magnuspaal.messagingservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

  @Query("SELECT c FROM Chat c JOIN c.chatUsers chatUser1 JOIN c.chatUsers chatUser2 WHERE chatUser1.user=:user1 AND chatUser2.user=:user2")
  Optional<List<Chat>> findPrivateChat(
      @Param("user1") User user1,
      @Param("user2") User user2
  );
}
