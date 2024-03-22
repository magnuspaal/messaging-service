package com.magnuspaal.messagingservice.userencryption;

import com.magnuspaal.messagingservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserEncryptionRepository extends JpaRepository<UserEncryption, Long> {
  @Query("SELECT u FROM UserEncryption u WHERE u.user=:user ORDER BY u.version DESC LIMIT 1")
  Optional<UserEncryption> findUserEncryption(
      @Param("user") User user
  );

  @Query("SELECT MAX(u.version) FROM UserEncryption u WHERE u.user=:user")
  Optional<Long> findUserEncryptionVersion(
      @Param("user") User user
  );
}
