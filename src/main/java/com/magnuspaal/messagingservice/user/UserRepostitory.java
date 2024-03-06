package com.magnuspaal.messagingservice.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepostitory extends JpaRepository<User, Long> {
}
