package com.magnuspaal.messagingservice.image;

import com.magnuspaal.messagingservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatImageRepository extends JpaRepository<ChatImage, Long> {
  @Query("SELECT i FROM ChatImage i WHERE i.filename=:filename AND i.user=:user")
  Optional<ChatImage> findChatImage(
      @Param("filename") String filename,
      @Param("user") User user
  );
}
