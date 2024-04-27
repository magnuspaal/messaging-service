package com.magnuspaal.messagingservice.controllers;

import com.magnuspaal.messagingservice.auth.AuthenticationService;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.chat.ChatService;
import com.magnuspaal.messagingservice.common.BaseResponse;
import com.magnuspaal.messagingservice.controllers.dto.ImageUploadResponseDTO;
import com.magnuspaal.messagingservice.controllers.exception.exceptions.NoUserEncryptionException;
import com.magnuspaal.messagingservice.image.ChatImage;
import com.magnuspaal.messagingservice.image.ChatImageRepository;
import com.magnuspaal.messagingservice.image.ImageUtils;
import com.magnuspaal.messagingservice.image.dto.AESEncryptionData;
import com.magnuspaal.messagingservice.image.dto.CompressedImage;
import com.magnuspaal.messagingservice.image.dto.UserChatImage;
import com.magnuspaal.messagingservice.message.MessageService;
import com.magnuspaal.messagingservice.messaging.MessagingHandler;
import com.magnuspaal.messagingservice.services.FileService;
import com.magnuspaal.messagingservice.user.User;
import com.magnuspaal.messagingservice.userencryption.UserEncryption;
import com.magnuspaal.messagingservice.userencryption.UserEncryptionService;
import com.magnuspaal.messagingservice.utils.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

  private final ChatService chatService;
  private final UserEncryptionService userEncryptionService;
  private final FileService fileService;
  private final ChatImageRepository chatImageRepository;
  private final MessagingHandler messagingHandler;
  private final AuthenticationService authenticationService;
  private final MessageService messageService;

  @PostMapping(value = "/upload/{id}", consumes="multipart/form-data")
  public ResponseEntity<BaseResponse> uploadImage(
      @CookieValue("authToken") String authToken,
      @RequestParam(name = "image") MultipartFile image,
      @PathVariable Long id
  ) throws NoUserEncryptionException {
    User sender = authenticationService.getAuthenticatedUser();
    Chat chat = chatService.getChatById(id);
    Long chatMessageId = messageService.getChatMessageCount(chat) + 1;

    CompressedImage compressedImage = ImageUtils.processImage(image, chat);

    // Encrypt image with symmetric key
    AESEncryptionData aesEncryptionData = ImageUtils.getSymmetricalEncryptionData();
    byte[] encryptedImage = ImageUtils.encryptImage(compressedImage.image(), aesEncryptionData);

    fileService.uploadFile(encryptedImage, compressedImage.filename(), authToken, chat, compressedImage.imageRatio());

    List<UserChatImage> userChatImages = new ArrayList<>();
    for (User user: chat.getUsers()) {
      UserEncryption userEncryption = userEncryptionService.getUserEncryption(user);
      PublicKey publicKey = RSAUtil.getPublicKey(userEncryption);

      // Encrypt symmetric key with users private key
      String aesKeyString = Base64.getEncoder().encodeToString(aesEncryptionData.key().getEncoded());
      byte[] encryptedKey = RSAUtil.encrypt(aesKeyString, publicKey);

      // Save encrypted symmetric key and image filename
      ChatImage chatImage = new ChatImage(user, encryptedKey, aesEncryptionData.iv().getIV(), compressedImage.filename());
      chatImageRepository.save(chatImage);

      userChatImages.add(new UserChatImage(user, chatImage));
    }

    for (UserChatImage userChatImage: userChatImages) {
      messagingHandler.handleImageMessage(chat, sender, userChatImage.user(), userChatImage.chatImage(), chatMessageId);
    }

    return ResponseEntity.ok(new ImageUploadResponseDTO(compressedImage.filename()));
  }
}
