package com.magnuspaal.messagingservice.userencryption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class UserEncryption extends BaseEntity {

  @Id
  @Column(name = "id", nullable = false)
  @SequenceGenerator(
      name = "user_encryption_sequence",
      sequenceName = "user_encryption_sequence",
      allocationSize = 1
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "user_encryption_sequence"
  )
  private Long id;

  @NotNull
  private Long version;

  @ManyToOne()
  @JoinColumn(name = "user_id")
  private User user;

  @JsonIgnore
  private byte[] publicKey;
  private byte[] encryptedPrivateKey;
  private byte[] salt;
  private byte[] iv;

  public UserEncryption(User user, @NotNull Long version, byte[] publicKey, byte[] encryptedPrivateKey, byte[] salt, byte[] iv) {
    this.user = user;
    this.version = version;
    this.publicKey = publicKey;
    this.encryptedPrivateKey = encryptedPrivateKey;
    this.salt = salt;
    this.iv = iv;
  }
}
