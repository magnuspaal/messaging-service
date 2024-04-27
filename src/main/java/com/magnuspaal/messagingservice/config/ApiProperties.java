package com.magnuspaal.messagingservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

@ConfigurationProperties(prefix = "application.api")
@Getter
public class ApiProperties {
  private final List<String> allowedOrigins;
  private final String fileServerUrl;

  @ConstructorBinding
  public ApiProperties(List<String> allowedOrigins, String fileServerUrl) {
    this.allowedOrigins = allowedOrigins;
    this.fileServerUrl = fileServerUrl;
  }
}
