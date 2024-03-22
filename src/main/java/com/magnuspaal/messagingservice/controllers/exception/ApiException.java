package com.magnuspaal.messagingservice.controllers.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiException extends Throwable {
  private HttpStatus statusCode;

  public ApiException(String detailMessage, HttpStatus statusCode) {
    super(detailMessage);
    this.statusCode = statusCode;
  }
}
