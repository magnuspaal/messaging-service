package com.magnuspaal.messagingservice.message;

import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
  @Query("SELECT m FROM ChatMessage m WHERE m.chat=:chat AND m.owner=:owner ORDER BY m.createdAt DESC LIMIT :limit OFFSET :offset")
  Optional<List<ChatMessage>> findChatMessages(
      @Param("owner") User user,
      @Param("chat") Chat chat,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset
  );

  @Query("SELECT MAX(m.chatMessageId) FROM ChatMessage m WHERE m.chat=:chat")
  Optional<Long> findChatMessageCount(
      @Param("chat") Chat chat
  );

  @Query("SELECT m FROM ChatMessage m WHERE m.chat=:chat AND m.chatMessageId=:chatMessageId")
  Optional<List<ChatMessage>> findByChatAndChatMessageId(
      @Param("chat") Chat chat,
      @Param("chatMessageId") Long chatMessageId
  );
}
