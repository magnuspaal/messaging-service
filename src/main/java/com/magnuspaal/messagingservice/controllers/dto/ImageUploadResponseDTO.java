package com.magnuspaal.messagingservice.controllers.dto;

import com.magnuspaal.messagingservice.common.BaseResponse;
import lombok.Data;

@Data
public class ImageUploadResponseDTO extends BaseResponse {
  private final String filename;

  public ImageUploadResponseDTO(String filename) {
    this.filename = filename;
  }
}
