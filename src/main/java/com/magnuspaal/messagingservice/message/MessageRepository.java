package com.magnuspaal.messagingservice.message;

import com.magnuspaal.messagingservice.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
  @Query("SELECT m FROM Message m WHERE m.chat=:chat ORDER BY m.createdAt DESC LIMIT :limit OFFSET :offset")
  Optional<List<Message>> findChatMessages(
      @Param("chat") Chat chat,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset
  );
}
