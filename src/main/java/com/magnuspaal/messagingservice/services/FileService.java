package com.magnuspaal.messagingservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

  private final ApiProperties apiProperties;

  public void uploadFile(byte[] file, String filename, String authToken, Chat chat, int imageRatio) {
    ByteArrayResource contentsAsResource = new ByteArrayResource(file) {
      @Override
      public String getFilename() {
        return filename;
      }
    };

    RestTemplate restTemplate = new RestTemplate();

    String fileServerUrl = apiProperties.getFileServerUrl();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
    headers.add("Cookie", "authToken=" + authToken);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", contentsAsResource);
    body.add("filename", filename);
    body.add("imageRatio", imageRatio);
    body.add("chat", chat.getId());
    body.add("users", chat.getUsers().stream().map((user -> user.getId().toString())).collect(Collectors.joining(",")));

    HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
    restTemplate.postForEntity(fileServerUrl + "/api/v1/chat/upload", entity, JsonNode.class);
  }
}
