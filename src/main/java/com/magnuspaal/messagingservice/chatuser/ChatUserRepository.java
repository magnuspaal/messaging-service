package com.magnuspaal.messagingservice.chatuser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatUserRepository extends JpaRepository<ChatUser, ChatUserKey> {
}
