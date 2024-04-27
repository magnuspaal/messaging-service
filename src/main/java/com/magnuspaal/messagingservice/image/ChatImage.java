package com.magnuspaal.messagingservice.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magnuspaal.messagingservice.common.BaseEntity;
import com.magnuspaal.messagingservice.message.ChatMessage;
import com.magnuspaal.messagingservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chat_image")
@SQLRestriction("deleted_at IS NULL")
public class ChatImage extends BaseEntity {
  @Id
  @SequenceGenerator(name = "chat_image_sequence", sequenceName = "chat_image_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_image_sequence")
  private Long id;
  @ManyToOne
  @JsonIgnore
  @JoinColumn(name="user_id")
  private User user;
  private byte[] key;
  private byte[] iv;
  private String filename;
  @JsonIgnore
  @OneToOne(mappedBy = "chatImage")
  private ChatMessage chatMessage;

  public ChatImage(User user, byte[] key, byte[] iv, String filename) {
    this.user = user;
    this.key = key;
    this.iv = iv;
    this.filename = filename;
  }
}
