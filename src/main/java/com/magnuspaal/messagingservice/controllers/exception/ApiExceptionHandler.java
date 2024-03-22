package com.magnuspaal.messagingservice.controllers.exception;

import com.magnuspaal.messagingservice.common.BaseResponse;
import com.magnuspaal.messagingservice.controllers.exception.dto.ApiExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(Throwable.class)
  public static ResponseEntity<BaseResponse> handleError(HttpServletRequest req, ApiException ex) {
    if (ex.getStatusCode() != null) {
      ArrayList<String> codes = new ArrayList<>(List.of(ex.getMessage()));
      return ResponseEntity.status(ex.getStatusCode()).body(new ApiExceptionResponse(codes));
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
