package com.magnuspaal.messagingservice.controllers.exception.dto;


import com.magnuspaal.messagingservice.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiExceptionResponse extends BaseResponse {
  private ArrayList<String> codes;
}