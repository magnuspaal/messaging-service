package com.magnuspaal.messagingservice.controllers.exception.exceptions;

import com.magnuspaal.messagingservice.controllers.exception.ApiException;
import com.magnuspaal.messagingservice.controllers.exception.ApiExceptionCode;
import org.springframework.http.HttpStatus;

public class NoUserEncryptionException extends ApiException {
  public NoUserEncryptionException() {
    super(ApiExceptionCode.NO_USER_ENCRYPTION, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
